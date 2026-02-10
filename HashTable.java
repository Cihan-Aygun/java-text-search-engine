public class HashTable<K, V> {


    private static class HashEntry<K, V> {
        K key;
        V value;
        boolean isDeleted = false;
        
        public HashEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private HashEntry<K, V>[] table;
    private int size = 0;
    private int capacity;
    private int collisionCount = 0; 
    private int probingType;
    private int hashType;
    private double loadFactor; 

    @SuppressWarnings("unchecked")
    
    public HashTable(int capacity, int probingType, int hashType, double loadFactor) {
        this.capacity = isPrime(capacity) ? capacity : nextPrime(capacity);
        this.probingType = probingType;
        this.hashType = hashType;
        this.loadFactor = loadFactor;
        this.table = new HashEntry[this.capacity];
    }

 
    public void put(K key, Object documentName) {
        
        if ((double) size / capacity >= loadFactor) {
            resize(capacity * 2);
        }

        String strKey = key.toString();
        int hash = calculateHash(strKey);
        int index = hash;
        int i = 0;
        int step = 1;

        if (probingType == 1) { 
            step = doubleHash(strKey);
        }

        while (table[index] != null) {
            
            if (!table[index].isDeleted && table[index].key.equals(key)) {
                
                WordNode node = (WordNode) table[index].value;
                node.addDocument((String) documentName);
                return;
            }

           
            collisionCount++;

            i++;
            if (probingType == 1) {
                index = (hash + i * step) % capacity;
            } else { 
                index = (index + 1) % capacity;
            }
        }

       
        WordNode newNode = new WordNode((String) key);
        newNode.addDocument((String) documentName);
        
        
        table[index] = new HashEntry<>(key, (V) newNode);
        size++;
    }

    public Object get(K key) {
        String strKey = key.toString();
        int hash = calculateHash(strKey);
        int index = hash;
        int i = 0;
        int step = 1;

        if (probingType == 1) step = doubleHash(strKey);

        while (table[index] != null) {
            if (!table[index].isDeleted && table[index].key.equals(key)) {
                return table[index].value;
            }
            i++;
            if (probingType == 1) index = (hash + i * step) % capacity;
            else index = (index + 1) % capacity;
            
            if (i > capacity) break; 
        }
        return null;
    }

    private int calculateHash(String key) {
        int hashVal;
        if (hashType == 1) hashVal = polynomialHash(key);
        else hashVal = simpleSummationHash(key);
        return Math.abs(hashVal % capacity);
    }

    private int simpleSummationHash(String key) {
        int sum = 0;
        for (int i = 0; i < key.length(); i++) sum += key.charAt(i);
        return sum;
    }

    private int polynomialHash(String key) {
        int z = 33;
        int hash = 0;
        for (int i = 0; i < key.length(); i++) 
            hash = (hash * z + key.charAt(i)) % capacity;
        return hash;
    }

    private int doubleHash(String key) {
        int hashVal = simpleSummationHash(key);
        int q = getPreviousPrime(capacity);
        int result = q - (Math.abs(hashVal) % q);
        return result == 0 ? 1 : result;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        newCapacity = nextPrime(newCapacity);
        HashEntry<K, V>[] oldTable = table;
        
        table = new HashEntry[newCapacity];
        capacity = newCapacity;
        size = 0;

        for (HashEntry<K, V> entry : oldTable) {
            if (entry != null && !entry.isDeleted) {
                
                putDirectly(entry.key, entry.value);
            }
        }
    }
    
    private void putDirectly(K key, V value) {
        String strKey = key.toString();
        int hash = calculateHash(strKey);
        int index = hash;
        int i = 0;
        int step = 1;
        if (probingType == 1) step = doubleHash(strKey);

        while (table[index] != null) {
            i++;
            if (probingType == 1) index = (hash + i * step) % capacity;
            else index = (index + 1) % capacity;
        }
        table[index] = new HashEntry<>(key, value);
        size++;
    }

    
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i * i <= n; i++) if (n % i == 0) return false;
        return true;
    }
    private int nextPrime(int n) { while (!isPrime(n)) n++; return n; }
    private int getPreviousPrime(int n) { n--; while (n > 2 && !isPrime(n)) n--; return n; }
    
    public int getCollisionCount() { return collisionCount; }
}