package main;
import java.util.Random;

public class L2Cache {
    private Word[] addresses;
    private Word[][] cache;
    private Processor processor;
    private int mostRecentAddress;
    public L2Cache(Processor processor)
    {
        addresses = new Word[]{new Word(2000),new Word(2000),new Word(2000),new Word(2000)};
        cache = new Word[4][8];
        for(int i = 0;i<4;i++)
        for(int j = 0;j<8;j++)
        cache[i][j] = new Word();
        this.processor = processor;
        mostRecentAddress = -1;
    }
    public boolean cacheHit(Word address)
    {
        int addressNumber = address.getSigned();
        for(int i = 0;i < 4; i++)
        {
            int storedAddressNumber = addresses[i].getSigned();
            if(storedAddressNumber <= addressNumber && storedAddressNumber+8 > addressNumber)
            return true;
        }
        return false;
    }
    public Word read(Word address)
    {
        int addressNumber = address.getSigned();
        for(int i = 0;i < 4; i++)
        {
            int storedAddressNumber = addresses[i].getSigned();
            if(storedAddressNumber <= addressNumber && storedAddressNumber+8 > addressNumber)
            {
                processor.addClockCycles(50);
                System.out.println("L2 Cache hit!");
                mostRecentAddress = i;
                return cache[i][addressNumber-storedAddressNumber];
            }
        }
        //cache miss, randomly select a slot to fill
        Random random = new Random();
        int cacheSpot = 0;
        boolean emptyCacheSpot = false;
        for(int i = 0; i < 4;i++)
        if(addresses[i].getSigned()==2000)
        {
            emptyCacheSpot = true;
            cacheSpot = i;
        }
        if(!emptyCacheSpot)
        cacheSpot = random.nextInt(4);
        System.out.println("L2 Cache miss!");
        processor.addClockCycles(350);
        //cache miss, read 8 words into memory starting with the input address
        for(int i = address.getSigned(); i < address.getSigned()+8;i++)
        {
            cache[cacheSpot][i-address.getSigned()] = MainMemory.read(new Word(i));
        }
        addresses[cacheSpot] = new Word(address.getSigned());
        mostRecentAddress = cacheSpot;
        return cache[cacheSpot][0];
    }
    /*
     * Writes a word to an address in the cache and in memory.
     */
    public void write(Word address, Word value)
    {
        //writes the value to memory
        MainMemory.write(address, value);
        //loads the value into cache including the 7 values afterwards.
        read(address);
    }
    public Integer getCacheArrayIndex(Word address)
    {
        int addressNumber = address.getSigned();

        for(int i = 0;i<4;i++)
        {
            int storedAddressNumber = addresses[i].getSigned();
            if(storedAddressNumber <= addressNumber && storedAddressNumber+8 > addressNumber)
            return i;
        }
        return null;
    }
    public Word[] getCacheArray(int index)
    {
        return cache[index];
    }
    public Word getAddress(int index)
    {
        System.out.println("GET ADDRESS: "+addresses[index]);
        return addresses[index];
    }
    public int getMostRecentAddress()
    {
        return mostRecentAddress;
    }

}
