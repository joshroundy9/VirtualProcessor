public class MainMemory {

    private static Word[] memory = new Word[1024];

    public static void refreshMemory()
    {
        memory = new Word[1024];
    }
    /*
     * Returns the word in memory at the given address.
     */
    public static Word read(Word address)
    {
        System.out.println("MAIN MEMORY READ: "+address.getSigned());
        Word word = new Word();
        int index = (int) address.getUnsigned();
        //if the index has not been initialized, return an empty word.
        if(memory[index] != null)
            word.copy(memory[index]);

        return word;

    }
    /*
     * Writes a given word into memory at a given address.
     */
    public static void write(Word address, Word value)
    {
        int index = (int)address.getUnsigned();
        /*if the memory index is not initialized,
         * we create a new word, set its value, then set
         * the memory index to point to it. */
        if(memory[index] == null)
        {
            Word word = new Word();
            word.copy(value);
            memory[index] = word;
        } else 
            memory[index].copy(value);
    }
    /*
     * Loads data from strings of 1s and 0s into memory.
     */
    public static void load(String[] data)
    {
        Word address = new Word();
        for(int i = 0;i<data.length;i++)
        {
            Word word = new Word();
            char c[] = data[i].toCharArray();
            for(int j = 0;j<32;j++)
            {
                Bit newBit = new Bit(null);
                if(c[j]=='1')
                newBit.set();
                else
                newBit.clear();
                word.setBit(j, newBit);
            }
            address.set(1023-i);
            write(address, word);
        }
    }
    public static Word[] getMemory()
    {
        return memory;
    }
}
