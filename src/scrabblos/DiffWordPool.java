package scrabblos;

public class DiffWordPool {
	int since;
	WordPool wordpool;
	
	public DiffWordPool(int since, WordPool wordpool) {
		super();
		this.since = since;
		this.wordpool = wordpool;
	}

	public int getSince() {
		return since;
	}

	public void setSince(int since) {
		this.since = since;
	}

	public WordPool getWordpool() {
		return wordpool;
	}

	public void setWordpool(WordPool wordpool) {
		this.wordpool = wordpool;
	}
	
	
}
