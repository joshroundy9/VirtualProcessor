public class Token {
    enum tokenType{
        MATH, ADD, SUBTRACT, MULTIPLY, AND, OR, NOT, XOR, COPY, HALT, BRANCH, JUMP,
        CALL, PUSH, LOAD, RETURN, STORE, PEEK, POP, INTERRUPT, EQUAL, UNEQUAL, GREATER, LESS
        ,GREATEROREQUAL,LESSOREQUAL, SHIFT, LEFT, RIGHT, NUMBER, REGISTER
        ,NEWLINE,ENDOFFILE, TWOR,THREER,DEST_ONLY,NOR,NOTEQUAL;
    }
    
        private tokenType type;
    
        private Integer value; 
    
        public Token(Integer value, tokenType type)
        {
            this.value = value;
            this.type = type;
        }
        public Token(tokenType type)
        {
            this.type = type;
            this.value = null;
        }
    
    
        
        /** Overrides the toString method and returns the value.
         * @return String 
         */
        public String toString()
        {
            return type.toString()+"("+value+")";
        }
        
        /** Returns the value of the token.
         * @return Integer
         */
        public Integer getValue()
        {
            return value;
        }
        
        /** Returns the tokens type.
         * @return tokenType 
         */
        public tokenType getType()
        {
            return type;
        }
        /** Sets the tokens type.
         */
        public void setType(tokenType type)
        {
            this.type=type;
        }
        public void print()
        {
            if(value != null)
            {
                System.out.println(type.toString()+"("+getValue()+")");
            } else {
                System.out.println(type.toString());
            }
        }
    }
    
