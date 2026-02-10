import java.util.ArrayList;

public class WordNode {
    
    String word;
    
    ArrayList<String> documents;
    
    ArrayList<Integer> frequencies;

    public WordNode(String word) {
        this.word = word;
        this.documents = new ArrayList<>();
        this.frequencies = new ArrayList<>();
    }

   
    public void addDocument(String docName) {
        int index = documents.indexOf(docName);
        if (index != -1) {
            
            frequencies.set(index, frequencies.get(index) + 1);
        } else {
            
            documents.add(docName);
            frequencies.add(1);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Search: ").append(word).append("\n");
        sb.append(documents.size()).append(" documents found\n");
        
        for (int i = 0; i < documents.size(); i++) {
            sb.append(documents.get(i)).append(" (").append(frequencies.get(i)).append(")");
            if (i < documents.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}