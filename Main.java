import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("BBC PERFORMANS TESTİ BAŞLIYOR ");
        System.out.println("Dosyalar taranıyor");

        File folder = new File("bbc");
        if (!folder.exists()) {
            System.out.println("HATA: 'bbc' klasörü bulunamadı.");
            return;
        }

       
        List<File> allFiles = new ArrayList<>();
        listAllFiles(folder, allFiles);
        System.out.println("Toplam dosya: " + allFiles.size());
        
       

       
        System.out.println("-".repeat(105));
        System.out.printf("| %-5s | %-5s | %-5s | %-12s | %-15s | %-10s | %-10s | %-10s |\n",
                "LF", "Hash", "Coll", "Collisions", "IndexTime(ns)", "Avg Search", "Min Search", "Max Search");
        System.out.println("-".repeat(105));

       
        double[] loadFactors = {0.5, 0.8};
        String[] hashNames   = {"SSF", "PAF"}; 
        String[] collNames   = {"LP", "DH"};  

        
        for (double lf : loadFactors) {
            for (int h = 0; h < hashNames.length; h++) {
                for (int c = 0; c < collNames.length; c++) {
                    
                  
                    System.gc();
                    
                    
                    
                    InvertedIndex index = new InvertedIndex(1000, c, h, lf);

                  
                    long tStart = System.nanoTime();
                    for (File f : allFiles) {
                        index.addDocument(f);
                    }
                    long tEnd = System.nanoTime();
                    long indexTime = tEnd - tStart;
                    int collisions = index.getCollisionCount();

                   
                    long[] searchResults = runSearchTest(index); // {Avg, Min, Max}

                  
                    System.out.printf("| %-5s | %-5s | %-5s | %-12d | %-15d | %-10d | %-10d | %-10d |\n",
                            lf, hashNames[h], collNames[c], 
                            collisions, indexTime, 
                            searchResults[0], searchResults[1], searchResults[2]);
                }
            }
        }
        System.out.println("-".repeat(105));
    }

    
    private static void listAllFiles(File directory, List<File> files) {
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) files.add(file);
                else if (file.isDirectory()) listAllFiles(file, files);
            }
        }
    }

    
    private static long[] runSearchTest(InvertedIndex index) {
        File searchFile = new File("1000.txt");
        long total = 0, min = Long.MAX_VALUE, max = Long.MIN_VALUE;
        int count = 0;

        if (searchFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(searchFile))) {
                String word;
                while ((word = br.readLine()) != null) {
                    word = word.trim();
                    if (word.isEmpty()) continue;

                    long t0 = System.nanoTime();
                    index.search(word);
                    long t1 = System.nanoTime();
                    long dt = t1 - t0;

                    total += dt;
                    count++;
                    if (dt < min) min = dt;
                    if (dt > max) max = dt;
                }
            } catch (Exception e) {}
        }
        
        long avg = (count > 0) ? (total / count) : 0;
        return new long[]{avg, min, max};
    }
}