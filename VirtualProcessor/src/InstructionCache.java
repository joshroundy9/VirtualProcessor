public class InstructionCache {
    //eight sequential words in memory
    private Word[] mainCache;
    //the address of the first word
    private Word address;
    private Processor processor;
    private L2Cache l2Cache;
    public InstructionCache(Processor processor, L2Cache l2Cache)
    {
        mainCache = new Word[]{new Word(),new Word(),
            new Word(),new Word(),new Word(),new Word(),
            new Word(),new Word()};
            //sets the address to be out of bounds forcing the first 8 addresses to be loaded into cache
            address = new Word(2000);
            this.processor = processor;
            this.l2Cache = l2Cache;
    }
    public Word read(Word address)
    {
        //cache hit
        System.out.println("Instruction cache read "+address.getSigned()+" stored address: "+this.address.getSigned());
        if(this.address.getSigned() <=address.getSigned() && this.address.getSigned()+8 > address.getSigned())
        {
            System.out.println("Instruction cache hit!");
        processor.addClockCycles(10);
        return mainCache[address.getSigned()-this.address.getSigned()];
        } else {
            //cache miss
            //check l2 cache
            System.out.println("Instruction cache miss!");
            Word returnValue = l2Cache.read(address);
            int l2CacheIndex = l2Cache.getMostRecentAddress();
            this.mainCache = l2Cache.getCacheArray(l2CacheIndex);
            this.address = l2Cache.getAddress(l2CacheIndex);
            processor.addClockCycles(50);
            return returnValue;
        }

    }
}
