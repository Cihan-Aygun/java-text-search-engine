import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

public class InvertedIndex {

    private HashTable<String, Object> table;
    private HashSet<String> stopWords;
    private String delimiterRegex;

    
    public InvertedIndex(int capacity, int method, int hashFunc, double loadFactor) {
        
        this.table = new HashTable<>(capacity, method, hashFunc, loadFactor);
        this.stopWords = new HashSet<>();
        
        loadStopWords("stop_words_en.txt");
        loadDelimiters("delimiters.txt");
    }

    
    public void addDocument(File file) {
        if (!file.exists()) return;
        try (Scanner scanner = new Scanner(file)) {
            if (this.delimiterRegex != null && !this.delimiterRegex.isEmpty()) scanner.useDelimiter(this.delimiterRegex);
            while (scanner.hasNext()) {
                String rawWord = scanner.next();
                String word = rawWord.toLowerCase().trim();
                if (!word.isEmpty() && !stopWords.contains(word)) table.put(word, file.getName());
            }
        } catch (IOException e) { }
    }

    public void search(String query) {
        
        table.get(query.toLowerCase().trim());
    }
    
    public int getCollisionCount() { return table.getCollisionCount(); }

    private void loadStopWords(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line; while ((line = br.readLine()) != null) stopWords.add(line.trim().toLowerCase());
        } catch (Exception e) {}
    }
    
    private void loadDelimiters(String filename) {
        StringBuilder sb = new StringBuilder("[");
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int c; while ((c = br.read()) != -1) if (!Character.isWhitespace((char)c)) sb.append("\\").append((char)c);
        } catch (Exception e) { sb.append("^a-zA-Z0-9"); }
        sb.append("\\s]+"); this.delimiterRegex = sb.toString();
    }
}