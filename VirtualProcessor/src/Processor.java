public class Processor {
    private Word pc, sp, instruction,
            opcode, rd, function, rs1, rs2, immediate, result;
    private Word registers[];
    private final Word r0;
    private Bit halted;
    private ALU alu;
    private RState currentState;
    private int currentClockCycle = 0;
    private InstructionCache instructionCache;
    private L2Cache l2Cache;

    private enum RState {
        TWO_R, THREE_R, DEST_ONLY, NO_R
    }

    public Processor() {
        registers = new Word[30];
        function = new Word();
        for (int i = 1; i < registers.length; i++)
            registers[i] = new Word();
        alu = new ALU();
        halted = new Bit(false);
        // program counter
        pc = new Word();
        // stack pointer
        sp = new Word();
        pc.set(0);
        sp.set(1024);
        r0 = new Word();
        registers[0] = r0;
        rd = new Word();
        result = new Word();
        currentClockCycle = 0;
        l2Cache = new L2Cache(this);
        instructionCache = new InstructionCache(this,l2Cache);

    }

    /*
     * Loops the methods fetch, decode, store, and execute.
     * This simulates the steps an actual processor takes.
     */
    public void run() throws Exception {
            while (!halted.getValue()) {
                System.out.println("Executing instruction.");
                fetch();
                decode();
                execute();
                store();
            }

    }
    public int getCurrentClockCycle()
    {
        return currentClockCycle;
    }

    /*
     * Takes an instruction from memory and splits it into
     * opcode, rd, function, rs1, rs2, and immediate
     * depending on the last two bits of the instruction.
     */
    public void decode() {
        Word mask = new Word(), instructionTemp = new Word();
        mask.set(31);
        opcode = instruction.and(mask);
        instructionTemp = instruction.rightShift(5);
        /*
         * sorts through bits to get SIA32 instruction format
         * uses enum to make it easier in execute
         */
        if (opcode.getBit(30).getValue()) {
            if (opcode.getBit(31).getValue())
                currentState = RState.TWO_R;
            else
                currentState = RState.THREE_R;
        } else {
            if (opcode.getBit(31).getValue())
                currentState = RState.DEST_ONLY;
            else
                currentState = RState.NO_R;
        }
        switch (currentState) {
            case THREE_R:
                rd = mask.and(instructionTemp);
                instructionTemp = instructionTemp.rightShift(5);
                // makes the mask only four long
                mask.getBit(27).clear();
                function = mask.and(instructionTemp);
                instructionTemp = instructionTemp.rightShift(4);
                // makes the mask five long again
                mask.getBit(27).set();
                rs1 = mask.and(instructionTemp);
                instructionTemp = instructionTemp.rightShift(5);
                rs2 = mask.and(instructionTemp);
                immediate = instructionTemp.rightShift(5);
                break;

            case TWO_R:
                rd = mask.and(instructionTemp);
                instructionTemp = instructionTemp.rightShift(5);
                // makes the mask only four long
                mask.getBit(27).clear();
                function = mask.and(instructionTemp);
                instructionTemp = instructionTemp.rightShift(4);
                // makes the mask five long again
                mask.getBit(27).set();
                rs1 = mask.and(instructionTemp);
                immediate = instructionTemp.rightShift(5);
                break;

            case DEST_ONLY:
                rd = mask.and(instructionTemp);
                instructionTemp = instructionTemp.rightShift(5);
                mask.setBit(27, new Bit(false));
                function = mask.and(instructionTemp);
                immediate = instructionTemp.rightShift(4);
                break;

            case NO_R:
                immediate = instructionTemp;
                break;
        }

    }

    /*
     * Executes the decoded instruction from memory.
     * Uses if statements to get the current instruction type.
     * 000 - Math
     * 001 - branch
     * 010 - call
     * 011 - push
     * 100 - load
     * 101 - store
     * 110 - pop/interrupt
     */
    public void execute() {
        // since we are only using the opcode Math we do not have to check for others
        // currently.
        
        if (opcode.getBit(27).getValue()) {
            if (opcode.getBit(28).getValue()) {
                // pop/interrupt
                interrupt();
            } else {
                if (opcode.getBit(29).getValue()) {
                    // store
                    storeFunction();
                } else {
                    // load
                    load();
                }
            }
        } else {
            if (opcode.getBit(28).getValue()) {
                if (opcode.getBit(29).getValue()) {
                    // push
                    push();
                } else {
                    // call
                    call();
                }
            } else {
                if (opcode.getBit(29).getValue()) {
                    branches();
                } else {
                    math();
                }
            }
        }
    }

    /*
     * Implements the Push specification from the SIA32 Instruction Definition Matrix
     */
    private void push() {
        switch (currentState) {
            case DEST_ONLY:
                sp.decrement();
                alu.setOp1(getRegister(rd));
                alu.setOp2(immediate);
                alu.doOperation(new Bit[] {
                        function.getBit(28), function.getBit(29),
                        function.getBit(30), function.getBit(31) });
                write(sp, alu.getResult());
                break;
            case NO_R:
                // do nothing
                break;
            case THREE_R:
                sp.decrement();
                alu.setOp1(getRegister(rs1));
                alu.setOp2(getRegister(rs2));
                alu.doOperation(new Bit[] {
                        function.getBit(28), function.getBit(29),
                        function.getBit(30), function.getBit(31) });
                write(sp, alu.getResult());
                break;
            case TWO_R:
                sp.decrement();
                alu.setOp1(getRegister(rd));
                alu.setOp2(getRegister(rs1));
                alu.doOperation(new Bit[] {
                        function.getBit(28), function.getBit(29),
                        function.getBit(30), function.getBit(31) });
                write(sp, alu.getResult());
                break;
        }
    }

    /*
     * Implements the call specification from the SIA32 Instruction Definition Matrix
     */
    private void call() {
        switch (currentState) {
            case DEST_ONLY:
                // Pushes pc onto the stack
                write(sp, pc);
                alu.setOp1(getRegister(rd));
                alu.setOp2(immediate);
                alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
                pc.copy(alu.getResult());
                break;
            case NO_R:
                write(sp, pc);
                pc.copy(immediate);
                break;
            case THREE_R:
                if (BoolOp(getRegister(rs1), getRegister(rs2), instruction)) {
                    write(sp, pc);
                    alu.setOp1(getRegister(rd));
                    alu.setOp2(immediate);
                    alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
                    pc.copy(alu.getResult());
                } else
                    write(sp, pc);
                break;
            case TWO_R:
                if (BoolOp(getRegister(rs1), getRegister(rd), instruction)) {
                    write(sp, pc);
                    alu.setOp1(pc);
                    alu.setOp2(immediate);
                    alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
                    pc.copy(alu.getResult());
                } else
                    write(sp, pc);
                break;
        }
    }

    /*
     * Implements the Load specification from the SIA32 Instruction Definition Matrix
     */
    private void load() {
        switch (currentState) {
            case DEST_ONLY:
                alu.setOp1(getRegister(rd));
                alu.setOp2(immediate);
                alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
                setRegister(rd, read(alu.getResult()));
            break;
            case NO_R:
                sp.increment();
                pc.copy(read(sp));
            break;
            case THREE_R:
                alu.setOp1(getRegister(rs1));
                alu.setOp2(getRegister(rs2));
                alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
                setRegister(rd, read(alu.getResult()));
            break;
            case TWO_R:
                alu.setOp1(getRegister(rs1));
                alu.setOp2(immediate);
                alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
                setRegister(rd, read(alu.getResult()));
            break;
        }
    }
    /*
     * Implements the Store specification from the SIA32 Instruction Definition Matrix
     */
    private void storeFunction() {
        switch (currentState) {
            case DEST_ONLY:
            System.out.println("STORE DEST ONLY: REGISTER: "+rd.getSigned()+" NUMBER: "+immediate.getSigned());
            write(getRegister(rd), immediate);
            break;
            case NO_R:
                //Do nothing, not used
            break;
            case THREE_R:
            alu.setOp1(getRegister(rd));
            alu.setOp2(rs1);
            alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
            write(alu.getResult(), getRegister(rs2));
            break;
            case TWO_R:
            
            alu.setOp1(getRegister(rd));
            alu.setOp2(immediate);
            alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
            System.out.println("STORE 2R: POS: "+alu.getResult().getSigned()+" VAL: "+getRegister(rs1).getSigned());
            write(alu.getResult(), getRegister(rs1));
            break;
        }
    }
    /*
     * Implements the Pop/interrupt specification from the SIA32 Instruction Definition Matrix
     */
    private void interrupt() {
        switch (currentState) {
            case DEST_ONLY:
            setRegister(rd, read(sp));
            sp.increment();
            break;
            case NO_R:
                //Do nothing, this assignment does not implement interrupt.
            break;
            case THREE_R:
            alu.setOp1(getRegister(rs1));
            alu.setOp2(getRegister(rs2));
            //adds register values
            alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
            alu.setOp2(alu.getResult());
            alu.setOp1(sp);
            //subtracts result from sp
            alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(true) });
            setRegister(rd, read(alu.getResult()));
            break;
            case TWO_R:
            alu.setOp1(getRegister(rs1));
            alu.setOp2(immediate);
            //adds register values
            alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
            alu.setOp2(alu.getResult());
            alu.setOp1(sp);
            //subtracts result from sp
            alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(true) });
            setRegister(rd, read(alu.getResult()));
            break;
        }
    }

    /*
     * Handles Boolean operations.
     * Acts as an if statement for the next line.
     * If true, skip the next line.
     */
    private void branches() {
        switch (currentState) {
            case DEST_ONLY:
                alu.setOp1(pc);
                alu.setOp2(immediate);
                // adds together
                alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
                pc.copy(alu.getResult());
                break;
            case NO_R:
                pc.copy(immediate);
                break;
            case THREE_R:
                if (BoolOp(getRegister(rs1), getRegister(rs2), function)) {
                    alu.setOp1(pc);
                    alu.setOp2(immediate);
                    // adds together
                    alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
                    pc.copy(alu.getResult());
                }
                break;
            case TWO_R:
                if (BoolOp(getRegister(rs1), getRegister(rd), function)) {
                    alu.setOp1(pc);
                    alu.setOp2(immediate);
                    // adds together
                    alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(false) });
                    pc.copy(alu.getResult());
                }
                break;
        }
    }

    /*
     * Uses the ALU to compute math problems given using the
     * SIA32 document.
     */
    private void math() {
        Bit[] mathFunction;
        switch (currentState) {
            case DEST_ONLY:
                setRegister(rd, immediate);
                System.out.println("COPY: NUMBER: "+immediate.getSigned()+" REGISTER: "+rd.getSigned()+" RESULT: "+getRegister(rd).getSigned());
                break;
            case NO_R:
                halt();
                break;
            case THREE_R:
                mathFunction = new Bit[] {
                function.getBit(28), function.getBit(29),
                function.getBit(30), function.getBit(31)};
                alu.setOp1(getRegister(rs1));
                alu.setOp2(getRegister(rs2));
                alu.doOperation(mathFunction);
                System.out.println("ALU:" + alu.result.getSigned());
                result.copy(alu.getResult());
                setRegister(rd, result);
                if(mathFunction[0].getValue())
                {
                    if(mathFunction[1].getValue()&&mathFunction[2].getValue())
                    currentClockCycle+=2;
                } else if(mathFunction[1].getValue()&&mathFunction[2].getValue()&&mathFunction[3].getValue())
                currentClockCycle+=10;
                break;
            case TWO_R:
                mathFunction = new Bit[] {
                function.getBit(28), function.getBit(29),
                function.getBit(30), function.getBit(31)};
                alu.setOp1(getRegister(rs1));
                alu.setOp2(getRegister(rs1));
                alu.doOperation(mathFunction);
                result.copy(alu.getResult());
                setRegister(rd, result);
                if(mathFunction[0].getValue())
                {
                    if(mathFunction[1].getValue()&&mathFunction[2].getValue())
                    currentClockCycle+=2;
                } else if(mathFunction[1].getValue()&&mathFunction[2].getValue()&&mathFunction[3].getValue())
                currentClockCycle+=10;
                break;
        }
    }

    /*
     * Stores the output of execute into the appropriate register.
     */
    public void store() {
        // stores to the register based on the rd
        System.out.print("OPCODE:"+opcode.getBit(29)+""+opcode.getBit(28)+""+opcode.getBit(27)+" ");
        if (currentState == RState.DEST_ONLY)
            System.out.print(" DEST_ONLY VALUE " + immediate.getSigned());
        else {
            System.out.print(" " + function.getSigned());
            if (currentState == RState.TWO_R) {
                System.out.print(" OPS REGISTER" + rs1.getSigned());
            } else if (currentState == RState.THREE_R) {
                System.out.print(" OPS REGISTER" + rs1.getSigned() + " REGISTER" + rs2.getSigned());
            }
        }
        if (currentState != RState.NO_R) {
            System.out.print(" TO REGISTER:" + rd.getSigned());
        }
        System.out.println();

        //only trigger when the function is math
        if (!function.getBit(27).getValue()&& !function.getBit(28).getValue()&&!function.getBit(29).getValue() 
        && currentState!=RState.NO_R && currentState != RState.DEST_ONLY)
        {
            System.out.println("TRIGGER");
            setRegister(rd, result);
        }
    }

    /*
     * Fetches an instruction from memory using the program counter then increments
     * the counter.
     */
    public void fetch() {
        // test for halted functionality:
        System.out.println(pc.getSigned());
        instruction = read(pc);
        pc.increment();
    }

    public void halt() {
        halted.set();
        System.out.println("Halted!");
    }

    public void unhalt() {
        halted.clear();
    }

    /**
     * @return Word
     */
    public Word getPC() {
        return pc;
    }

    /**
     * @return Word
     */
    public Word getInstruction() {
        return instruction;
    }

    
    /** 
     * @param register
     * @return Word
     */
    public Word getRegister(Word register) {
        int index = register.getSigned();
        System.out.println("GET REGISTER " + index + " VALUE" + registers[index].getSigned());
        if (index == 0)
            return r0;
        else
            return registers[index];
    }

    
    /** 
     * @param register
     * @param value
     */
    public void setRegister(Word register, Word value) {
        int index = register.getSigned();
        System.out.println("SET REGISTER :" + index + " VALUE:" + value.getSigned());
        // stops an attempt to modify r0
        if (index != 0)
            registers[index].copy(value);
        System.out.println(registers[index].getSigned());
    }

    public Word[] getRegisters() {
        return registers;
    }

    private boolean BoolOp(Word word1, Word word2, Word instruction) {
        alu.setOp1(word1);
        alu.setOp2(word2);
        System.out.println("BOOLOP: "+word1.getSigned()+" "+instruction+" "+word2.getSigned());
        // subtracts the words so we can check for bool conditions easier
        alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(true) });
        Word addResult = new Word();
        addResult.copy(alu.getResult());

        if (instruction.getBit(29).getValue()) {
            if (instruction.getBit(31).getValue()) {
                // less than or equal
                return addResult.getBit(0).getValue() || addResult.equals(new Word());
            } else {
                // greater than
                return !addResult.getBit(0).getValue();
            }

        } else {
            if (instruction.getBit(30).getValue()) {
                if (instruction.getBit(31).getValue()) {
                    // greater or equal
                    System.out.println("Output: "+(!addResult.getBit(0).getValue() || addResult.equals(new Word())));
                    return !addResult.getBit(0).getValue() || addResult.equals(new Word());
                } else {
                    // less than
                    return addResult.getBit(0).getValue();
                }
            } else {
                if (instruction.getBit(31).getValue()) {
                    // not equal, if the two words subtracted from eachother do not equal zero
                    return !addResult.equals(new Word());
                } else {
                    // equals
                    return addResult.equals(new Word());
                }
            }
        }
    }
    public Word read(Word address)
    {
        return instructionCache.read(address);
    }
    public void write(Word address, Word value)
    {
        l2Cache.write(address, value);
    }
    public void addClockCycles(int clockCycles)
    {
        System.out.println("ADDED "+clockCycles+" CLOCK CYCLES.");
        currentClockCycle+=clockCycles;
    }
}
