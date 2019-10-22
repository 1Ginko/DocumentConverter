package Pojos;

public class Phrase {
	
	private String source;
	private String translate;

	public Phrase(String source,String translate) {
		this.source = source;
		this.translate = translate;		
	}
	
	public String getSource() {
		return this.source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getTranslate() {
		return this.translate;
	}
	
	public void setTranslate(String translate) {
		this.translate = translate;
	}
}
