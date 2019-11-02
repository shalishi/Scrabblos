package thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import scrabblos.Letter;
import scrabblos.LetterPool;
import scrabblos.Word;
import scrabblos.WordPool;

class MotorA {

	private static MotorA motor = new MotorA();
	private static boolean ROUND_FINISH_FLAG = false;
	static int TIME_UNIT_PER_ROUND = 1;
	static int MAX_ROUND = 10;
	static int CLIENT_QTY = 12;
	static int POLITTICIAN_QTY = 5;
	private static LetterPool letter_pool = new LetterPool(0,1,new ArrayList<Letter>());
	private static WordPool word_pool = new WordPool(0,1,new ArrayList<Word> ());
	private static final java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
	private static Map<String,Boolean> clients_states = new HashMap<String,Boolean>();
	private static Map<String,Boolean> politicians_states = new HashMap<String,Boolean>();
	
	public static Map<String, Boolean> getClients_states() {
		lock.lock();
		Map<String,Boolean> cs = clients_states;
		lock.unlock();
		return cs;
	}

	public static void setClients_states(Map<String, Boolean> clients_states) {
		lock.lock();
		MotorA.clients_states = clients_states;
		lock.unlock();
	}

	public static Map<String, Boolean> getPoliticians_states() {
		lock.lock();
		Map<String,Boolean> ps = politicians_states;
		lock.unlock();
		return ps;
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
	
	public static void updatePoliticianState(String s,boolean b) {
		lock.lock();
		politicians_states.put(s,b);
		Iterator iter = politicians_states.entrySet().iterator();
		Boolean passNextRound = true;
    	//System.out.println("******************************************************politician states******************************************************");
    	//System.out.println("s :"+s +" b:"+ b);
	        while (iter.hasNext()) {
	            Map.Entry<String,Boolean> ele = (Map.Entry<String,Boolean>) iter.next();
	            if (ele.getValue()==false) {
	            	passNextRound=false;
	            	//break;
	            	
	            }
	            //System.out.println(ele.getValue() + ": " +ele.getKey());
	        }
	    if(passNextRound)passNextRound();
		lock.unlock();
	}
	

	public static void passNextRound() {
		lock.lock();
		System.out.println("******************************************************pass to next round******************************************************");
		int cp = getLetter_pool().getCurrent_period();
		letter_pool.setCurrent_period(++cp);
		word_pool.setCurrent_period(++cp);
		lock.unlock();
	}
	
	public static void updateClientState(String s,boolean b) {
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

	public static void setWord_pool(WordPool word_pool) {
		lock.lock();
		MotorA.word_pool = word_pool;
		lock.unlock();
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

	public void showLetterPool() {
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

	public void addWord(Word w) {
		lock.lock();
		word_pool.getWords().add(w);
		lock.unlock();

	}

	public void showWordPool() {
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
	
	public void showPoliticiansState() {
		lock.lock();
		try {
			Iterator iter = politicians_states.entrySet().iterator();
	    	System.out.println("******************************************************politician states******************************************************");
		        while (iter.hasNext()) {
		            Map.Entry<String,Boolean> ele = (Map.Entry<String,Boolean>) iter.next();
		            System.out.println(ele.getValue() + ": " +ele.getKey());
		        }
		    	System.out.println("******************************************************finish politician states******************************************************");
		} finally {
			lock.unlock();
		}

	}

	/*
	public void jugement() {
		lock.lock();
		ArrayList<Word> wordpool = getWord_pool();
		ArrayList<Word> word = new ArrayList<Word>();
		int max = 0;
		for (Word w : wordpool) {
			if (w.getWord().size() > max) {
				word.clear();
				word.add(w);
				max = w.getWord().size();
			}
			if(w.getWord().size() == max) {
				word.add(w);
			}
		}
		Word winner = word.get(word.size()-1);
		String str =  winner.getPoliticien() +" has won, the word is"+ winner.toWord() +" , win 20points and have " +winner.getWord().size() +"letters";
		for(Letter l : winner.getWord()) {
			str +=" ,"+l.getAuthor() +" got "+(double)(80/winner.getWord().size()) +"points";
		}
		
		lock.unlock();
	}
	*/

}
