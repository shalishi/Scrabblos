package thread;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;

import scrabblos.Letter;
import scrabblos.LetterPool;
import scrabblos.Word;
import scrabblos.WordPool;

class MotorA {

	private static MotorA motor = new MotorA();
	private static boolean ROUND_FINISH_FLAG = true;
	static int TIME_UNIT_PER_ROUND = 1 * 2;
	static int MAX_ROUND = 10;
	static int CLIENT_QTY = 12;
	static int POLITTICIAN_QTY = 10;
	private static LetterPool letter_pool = new LetterPool(0, 1, new ArrayList<Letter>());
	private static WordPool word_pool = new WordPool(0, 1, new ArrayList<Word>());
	private static final java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
	private static Map<String, Boolean> clients_states = new HashMap<String, Boolean>();
	private static Map<String, Boolean> politicians_states = new HashMap<String, Boolean>();
	private static ArrayList<Block> blockchaines = new ArrayList<Block>();

	public static Map<String, Boolean> getClients_states() {
		lock.lock();
		Map<String, Boolean> cs = clients_states;
		lock.unlock();
		return cs;
	}

	public static void setClients_states(Map<String, Boolean> clients_states) {
		lock.lock();
		MotorA.clients_states = clients_states;
		lock.unlock();
	}

	public static Boolean getPoliticians_states(String pk) {
		lock.lock();
		Map<String, Boolean> ps = politicians_states;
		boolean state = ps.get(pk);
		lock.unlock();
		return state;
	}

	public static void registerPolitician(String s) {
		lock.lock();
		politicians_states.put(s, false);
		lock.unlock();
	}

	public static void registerClient(String s) {
		lock.lock();
		clients_states.put(s, false);
		lock.unlock();
	}

	public static void updatePoliticianState(String s, boolean b) {
		lock.lock();
		politicians_states.put(s, b);
		lock.unlock();
	}

	public static boolean flagPassNextRound() {
		lock.lock();
		// showPoliticiansState();
		boolean OLD_ROUND_FINISH_FLAG = ROUND_FINISH_FLAG;
		boolean passNextRound = politicians_states.values().stream().distinct().limit(2).count() < 2;
		ROUND_FINISH_FLAG = passNextRound;
		if (!OLD_ROUND_FINISH_FLAG && ROUND_FINISH_FLAG)
			passNextRound();
		lock.unlock();
		return passNextRound;
	}

	public static void passNextRound() {
		lock.lock();

		System.out.println(
				"******************************************************pass to next round******************************************************");
		System.out.println("**************************period lp current " + letter_pool.getCurrent_period()
				+ "**********************************");
		// System.out.println("**************************period wp current
		// "+word_pool.getCurrent_period()+"**********************************");
		//updateBlockChaine();
		int cp = letter_pool.getCurrent_period() + 1;
		letter_pool.setCurrent_period(cp);
		word_pool.setCurrent_period(cp);
		Iterator<Entry<String, Boolean>> it = politicians_states.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Boolean> e = it.next();
			politicians_states.put(e.getKey(), false);
		}
		//jugement();

		lock.unlock();
	}

	public static ArrayList<Word> getBlockByPoliticien(String s) {
		lock.lock();
		for (Block b : MotorA.blockchaines) {
			if (b.getPoliticien() == s) {

				return b.getBlock();
			}
		}
		ArrayList<Word> bloc = new ArrayList<Word>();
		Block newblock = new Block(bloc, s);
		MotorA.blockchaines.add(newblock);
		lock.unlock();
		return bloc;
	}

	public static void updateBlockChaine() {
		lock.lock();
		ArrayList<Word> wordpool = getCurrentWord_pool();
		for (Word w : wordpool) {
			ArrayList<Word> bloc = getBlockByPoliticien(w.getPoliticien());
			bloc.add(w);
		}
		lock.unlock();
	}

	public static void updateClientState(String s, boolean b) {
		lock.lock();
		clients_states.put(s, b);
		lock.unlock();
	}

	public static void setPoliticians_states(Map<String, Boolean> politicians_states) {
		lock.lock();
		MotorA.politicians_states = politicians_states;
		lock.unlock();
	}

	public static boolean getROUND_FINISH_FLAG() {
		lock.lock();
		boolean f = ROUND_FINISH_FLAG;
		lock.unlock();
		return f;
	}

	public static void setROUND_FINISH_FLAG(boolean flag) {
		lock.lock();
		MotorA.ROUND_FINISH_FLAG = flag;
		lock.unlock();

	}

	public static LetterPool getLetter_pool() {
		lock.lock();
		LetterPool lp = letter_pool;
		lock.unlock();
		return lp;
	}

	public static ArrayList<Letter> getCurrentLetter_pool() {
		lock.lock();
		LetterPool lp = letter_pool;
		ArrayList<Letter> res = new ArrayList<Letter>();
		for (Letter l : lp.getLetters()) {
			if (l.getPeriod() == lp.getCurrent_period()) {
				res.add(l);
			}
		}
		lock.unlock();
		return res;
	}

	public static void setLetter_pool(LetterPool letter_pool) {
		lock.lock();
		MotorA.letter_pool = letter_pool;
		lock.unlock();
	}

	public static WordPool getWord_pool() {
		lock.lock();
		WordPool wp = word_pool;
		lock.unlock();
		return wp;
	}

	public static ArrayList<Word> getCurrentWord_pool() {
		lock.lock();
		WordPool wp = word_pool;
		ArrayList<Word> res = wp.getCurrentPeriodWords();
		// System.out.println("period "+getCurrentPeriod() +" wordPool
		// size"+wp.getWords().size()+"current wordPool size"+res.size());
		lock.unlock();
		return res;
	}

	public static ArrayList<Word> getLastWord_pool() {
		lock.lock();
		WordPool wp = word_pool;
		int p = getCurrentPeriod() - 1;
		ArrayList<Word> res = wp.getWordsByPeriod(p);
		// System.out.println("last period "+getCurrentPeriod() +" wordPool
		// size"+wp.getWords().size()+"last wordPool size"+res.size());
		lock.unlock();
		return res;
	}

	public static void setWord_pool(WordPool word_pool) {
		lock.lock();
		MotorA.word_pool = word_pool;
		lock.unlock();
	}

	public static int getCurrentPeriod() {
		lock.lock();
		// System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@word_pool period
		// "+word_pool.getCurrent_period() +" letter_pool
		// period"+letter_pool.getCurrent_period());
		int p = letter_pool.getCurrent_period();
		lock.unlock();
		return p;
	}

	private MotorA() {
	}

	public static MotorA getMotorA() {
		return motor;
	}

	public static Lock getLock() {
		return lock;
	}

	public static void addLetter(Letter s) {
		lock.lock();
		letter_pool.getLetters().add(s);
		lock.unlock();
	}

	public static void showLetterPool() {
		lock.lock();
		try {
			System.out.println("show letter pool-------------------------------------------------");
			if (letter_pool.getLetters().size() > 0) {
				for (Letter s : letter_pool.getLetters()) {
					System.out.println(s.toString());
				}
			}
			System.out.println("finish show letter pool------------------------------------------");
		} finally {
			lock.unlock();
		}

	}

	public static void addWord(Word w) {
		lock.lock();
		word_pool.getWords().add(w);
		lock.unlock();

	}

	public static void showWordPool() {
		lock.lock();
		try {
			System.out.println("show WORD pool-------------------------------------------------");
			if (word_pool.getWords().size() > 0) {
				for (Word w : word_pool.getWords()) {
					System.out.println(w.toString());
				}
			}
			System.out.println("finish show WORD pool------------------------------------------");
		} finally {
			lock.unlock();
		}

	}

	public static void showPoliticiansState() {
		lock.lock();
		try {
			Iterator iter = politicians_states.entrySet().iterator();
			System.out.println(
					"******************************************************politician states******************************************************");
			while (iter.hasNext()) {
				Map.Entry<String, Boolean> ele = (Map.Entry<String, Boolean>) iter.next();
				System.out.println(ele.getValue() + ": " + ele.getKey());
			}
			System.out.println(
					"******************************************************finish politician states******************************************************");
		} finally {
			lock.unlock();
		}

	}
	
	public static void judge() {
		lock.lock();
		ArrayList<Word> currentWords = getLastWord_pool();
		int maxSize = 0;
		int maxSizeIndex = 0;
		if(currentWords.size()>0) {
			for(Word w : currentWords) {
				String str = w.getHash();
				int size = getPreSize(str);
				size += w.getWord().size();
				if (size > maxSize) {
					maxSize = size;
					maxSizeIndex = currentWords.indexOf(w);
				}
			}
			String winner = currentWords.get(maxSizeIndex).getSignature();
			System.out.println("maxBlock is "+winner + "maxSize is "+ maxSize);
			for(int i = MAX_ROUND; i>=0; i--){
				for (Word w : word_pool.getWords()) {
					if(w.getSignature()==winner) {
						System.out.println("politicien "+ w.getPoliticien() + " win "+ w.getWord().size() +" points.");
						winner = w.getHash();
						for(Letter l :w.getWord()) {
							System.out.println("client "+ l.getAuthor() + " wins "+ 1 +" points.");
						}
					}
				}
			}
		}
		
		lock.unlock();
	}

	private static int getPreSize(String hash) {
		lock.lock();
		MessageDigest digest;
		try {
			for (Word w : word_pool.getWords()) {
				digest = MessageDigest.getInstance("SHA-256");
				String sighash = w.getSignature();//Utils.bytesToHex(digest.digest((w.getSignature()).getBytes()));
				if (sighash == hash) {
					int size = w.getWord().size();
					if (w.getPeriod() > 0) {
						hash = w.getHash();
						//System.out.println("size  = " + size);
						return size + getPreSize(hash);
					}
					if (w.getPeriod() == 0) {
						//System.out.println("size  = " + size);
						return size;
					}
				}
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lock.unlock();
		return 0;

	}
	
	public static void jugement() {
		lock.lock();
		System.out.println("maxBlock is ");
		int maxSize = -1;
		int maxBlock = -1;
		for (int i = 0; i < blockchaines.size(); i++) {
			int size = 0;
			ArrayList<Word> words = blockchaines.get(i).getBlock();
			for (Word w : words) {
				size += w.getWord().size();
			}
			
			if (size > maxSize) {
				maxSize = size;
				maxBlock = i;
			}
			System.out.println(blockchaines.get(i).getPoliticien() + "'s block have size of " + size);
			System.out.println("maxBlock is "+maxBlock + "maxSize is "+ maxSize);
		}
		
		if (maxBlock >= 0) {

			Block winner = blockchaines.get(maxBlock);
			String str = winner.getPoliticien() + " has won, has " + winner.getBlock().size() + "blocks";
			String str2 = ", has " + maxSize + "letters";
			System.out.println(str + str2);
			ArrayList<Word> words = winner.getBlock();
			ArrayList<Letter> letters = new ArrayList<Letter>();
			for (Word w : words) {
				letters.addAll(w.getWord());
			}

			HashMap<String, Integer> authorp = new HashMap<String, Integer>();
			for (int i = 0; i < letters.size(); i++) {
				System.out.println((i + 1) + "eme letter " + letters.get(i).getLetter() + " author is "
						+ letters.get(i).getAuthor());
				authorp.put(letters.get(i).getAuthor(), 0);
			}
			System.out.println("have "+authorp.size()+" of differents authors");

			for (int i = 0; i < letters.size(); i++) {
				Iterator iter = authorp.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, Integer> ele = (Map.Entry<String, Integer>) iter.next();
					if (ele.getKey() == letters.get(i).getAuthor()) {
						int nbr = ele.getValue() + 1;
						authorp.put(letters.get(i).getAuthor(), nbr);
						//break;
					}
				}
			}

			Iterator iter2 = authorp.entrySet().iterator();
			while (iter2.hasNext()) {
				Map.Entry<String, Integer> ele = (Map.Entry<String, Integer>) iter2.next();
				System.out.println(ele.getKey() + " has " + ele.getValue() + " letters in this word");
			}
		}

			lock.unlock();
	}

}