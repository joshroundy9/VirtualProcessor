public class Bit {
    private Boolean value;
    public Bit(Boolean value)
    {
        this.value = value;
    }
    public void set()
    {
        this.value = true;
    }
    public void toggle()
    {
        value = !value;
    }
    public void clear()
    {
        value = false;
    }
    public Boolean getValue()
    {
        return value;
    }
    public Bit and(Bit other)
    {
        if(value)
            if(other.getValue())
            return new Bit(true);
        return new Bit(false);
    }
    public Bit or(Bit other)
    {
        if(value)
        return new Bit(true);
        if(other.getValue())
        return new Bit(true);

        return new Bit(false);
    }
    public Bit xor(Bit other)
    {
        return new Bit(this.value!=other.value);
    }
    public Bit not()
    {
        return new Bit(!this.value);
    }
    public String toString()
    {
        return this.value ? "t" : "f";
    }
}
