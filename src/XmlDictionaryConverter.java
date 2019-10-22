import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import org.w3c.dom.Element;

import Pojos.Phrase;

public class XmlDictionaryConverter {
	private static final CharSequence DIVIDER = "-";
	private static final CharSequence SEMICOLON = ";";
	private static final CharSequence STAR = "*";
	private static final CharSequence LEFT = "(";
	private static final CharSequence RIGHT = ")";
	private static final CharSequence POINT = ".";
	private static final CharSequence COMA = ",";
	private static final CharSequence BRACE = "\"";
	private static final String ENTER = "\n\n";

	private static Boolean sNext = true;
	private static Scanner sInput;
	private File mOutputFile;
	private Document mXmlReadDocument;

	public XmlDictionaryConverter(String xmlFileName, String outputFileName)
			throws IOException, ParserConfigurationException, SAXException {
		File xmlFile = getXmlFile(xmlFileName);
		if (xmlFile != null) {
			this.mXmlReadDocument = getXmlDocument(xmlFile);
			this.mOutputFile = getFile(outputFileName);
		}
	}

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		while (sNext)
			XmlDictionaryConverter.startInput();
	}

	public static void startInput() throws IOException, ParserConfigurationException, SAXException {
		sInput = new Scanner(System.in);
		System.out.print("Enter XML file name : ");
		String xmlFileName = sInput.nextLine();
		System.out.print("Enter output file name : ");
		String outputFileName = sInput.nextLine();
		XmlDictionaryConverter converter = new XmlDictionaryConverter(xmlFileName, outputFileName);
		converter.readTitle();
	}

	public void readTitle() throws IOException {
		if (mXmlReadDocument == null) return;	
		ArrayList<Phrase> phraseList = new ArrayList<Phrase>();
		Phrase phrase = new Phrase("", "");

		mXmlReadDocument.getDocumentElement().normalize();
		NodeList nList = mXmlReadDocument.getElementsByTagName("ar");
		if (nList != null && nList.getLength() > 0) {
			for (int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				String source = node.getLastChild().getTextContent().trim();
				String translate = node.getFirstChild().getTextContent().trim();
				phrase = new Phrase(source, translate);
				phraseList.add(phrase);
			}
			writeToFile(phraseList, mOutputFile);
		} else 
			System.out.println("Empty Node List");
	}

	private Document getXmlDocument(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(file);
	}

	private void writeToFile(ArrayList<Phrase> list, File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
		Collections.shuffle(list);
		for (int i = 0; i < list.size(); i++) {
			Phrase item = list.get(i);
			System.out.println("Writing : " + calcCurrentProgress(i, list.size()));
			String processedLine = processDictionary(item.getTranslate());
			if (checkEntry(item.getSource(), processedLine))
				writer.write(item.getSource() + "|" + processedLine + "\n");
		}
		writer.close();
		restart();
	}

	private String processDictionary(String translate) {
		String result = translate;
		if (result.contains(SEMICOLON))
			result = translate.substring(0, translate.indexOf(SEMICOLON.charAt(0)));
		else if (result.contains(COMA))
			result = translate.substring(0, translate.indexOf(COMA.charAt(0)));
		return result;
	}

	private Boolean checkEntry(String source, String translate) {
		if (source.length() < 25 && translate.length() < 25)
			if (!containsStar(source) && !containsStar(translate))
				if (!containsNum(source) && !containsNum(translate))
					if (!containsBracers(source) && !containsBracers(translate))
						if (!containsDividers(source) && !containsDividers(translate))
							if (!containsPoint(source) && !containsPoint(translate))
								if (!containsComa(source) && !containsComa(translate))
									if (!containsBrace(source) && !containsBrace(translate))
										return true;
		return false;
	}

	private void restart() {
		System.out.println("Continue ?\n Yes - Enter | No - print 'exit' ");
		String answer = sInput.nextLine();
		if (answer.equals("exit"))
			System.exit(0);

	}

	private File getFile(String name) throws IOException {
		File file = new File(name);
		if (!file.exists())
			file.createNewFile();
		return file;
	}

	private File getXmlFile(String name) throws IOException {
		File file = new File(name);
		if (!file.exists()) {
			System.out.println("File not found : " + name);
			return null;
		}
		return file;
	}

	private Boolean containsNum(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i)))
				return true;
		}
		return false;
	}

	private int calcCurrentProgress(int index, int size) {
		return index * 100 / size;
	}

	private Boolean containsBracers(String str) {
		return str.contains(LEFT) || str.contains(RIGHT);
	}

	private Boolean containsDividers(String str) {
		return str.contains(DIVIDER);
	}

	private Boolean containsPoint(String str) {
		return str.contains(POINT);
	}

	private Boolean containsComa(String str) {
		return str.contains(COMA);
	}

	private Boolean containsBrace(String str) {
		return str.contains(BRACE);
	}

	private Boolean containsStar(String str) {
		return str.contains(STAR);
	}

}
