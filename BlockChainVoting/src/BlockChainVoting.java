import java.util.*;
import java.security.MessageDigest;

class Block {
    int index;
    long timestamp;
    String voterID;
    String candidate;
    String previousHash;
    String hash;

    public Block(int index, long timestamp, String voterID, String candidate, String previousHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.voterID = voterID;
        this.candidate = candidate;
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        try {
            String dataToHash = index + Long.toString(timestamp) + voterID + candidate + previousHash;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashbytes = digest.digest(dataToHash.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashbytes) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    @Override
    public String toString() {
        return "Block{index=" + index + ", timestamp=" + timestamp + ", voterID='" + voterID +
                "', candidate='" + candidate + "', hash='" + hash + "'}";
    }
}

class Blockchain {
    private List<Block> blockchain = new ArrayList<>();
    private String previousHash = "0";
    private Set<String> votedVoters = new HashSet<>();

    public Blockchain() {
        blockchain.add(createBlock(0, "voter0", "CandidateA"));
    }

    public Block createBlock(int index, String voterID, String candidate) {
        long timestamp = System.currentTimeMillis();
        Block newBlock = new Block(index, timestamp, voterID, candidate, previousHash);
        previousHash = newBlock.hash; // Use the correct variable name
        return newBlock;
    }

    public void addBlock(String voterID, String candidate) {
        if (votedVoters.contains(voterID)) {
            System.out.println("Error: Voter ID " + voterID + " has already voted.");
            return;
        }
        votedVoters.add(voterID);
        int index = blockchain.size();
        Block newBlock = createBlock(index, voterID, candidate);
        blockchain.add(newBlock);
        System.out.println("Vote successfully added: " + newBlock);
    }

    public void showBlockchain() {
        for (Block block : blockchain) {
            System.out.println(block);
        }
    }

    public void calculateTotalVotes() {
        Map<String, Integer> voteCounts = new HashMap<>();
        for (Block block : blockchain) {
            voteCounts.put(block.candidate, voteCounts.getOrDefault(block.candidate, 0) + 1);
        }
        System.out.println("Total votes per candidate:");
        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

public class BlockChainVoting { // Added class declaration
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Blockchain blockchain = new Blockchain();

        // Voting loop
        while (true) {
            System.out.println("Enter voter ID (or type 'exit' to quit): ");
            String voterId = scanner.nextLine();
            if ("exit".equalsIgnoreCase(voterId)) {
                break;
            }

            System.out.println("Enter candidate name: ");
            String candidate = scanner.nextLine();

            blockchain.addBlock(voterId, candidate);
        }

        System.out.println("Displaying Blockchain: ");
        blockchain.showBlockchain();

        // Calculate and display total votes
        blockchain.calculateTotalVotes();

        scanner.close(); // Close the scanner to prevent resource leaks
    }
}
