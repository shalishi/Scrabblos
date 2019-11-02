package thread;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

import scrabblos.Letter;
import scrabblos.LetterPool;
import scrabblos.Word;
import scrabblos.WordPool;

class MotorA {

	private static MotorA motor = new MotorA();
	private static boolean flag = true;
	private static LetterPool letter_pool = new LetterPool(0,1,new ArrayList<Letter>());
	private static WordPool word_pool = new WordPool(0,1,new ArrayList<Word> ());
	private static final java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();

	public static boolean isFlag() {
		lock.lock();
		boolean f = flag;
		lock.unlock();
		return f;
	}

	public static void setFlag(boolean flag) {
		lock.lock();
		MotorA.flag = flag;
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
