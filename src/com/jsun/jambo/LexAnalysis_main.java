package com.jsun.jambo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.*;




public class LexAnalysis_main {

	static List<String> k;//k数组 关键字表
	static Map<String,String> s; //s数组 分界符表<包括分界符、算术运算符和关系运算符>
	static List<String> id;//id数组 存放标识符
	static List<Integer> ci; //ci数组存放常数    
	List<String> instring;//instring数组 数组为输入源程序的单词缓存
	static List <outputStr> outtoken;// outtoken数组   记录为输出内部表示缓存
	static char ch[] = new char[65530];//记录读取后的字符
	char charTemp = ' ';
	static int temp = 0; //定义当前识别的字符位置
	static int line = 1; //定义当前行，遇转行符号++
	static LinkedList<Character> qCharacters; //使用链表取识别的字符


	
	public static void main(String[] args) throws IOException {
		initList(); //初始化各表
		String fileName = "F:\\PLSourceCode"+File.separator+""+"pltest.txt"; //读取源文件位置
		File file = new File(fileName);
		int length = readFile(file);
		qCharacters = new LinkedList<>();
		for (int i = 0; i < length; i++) {
				qCharacters.add(ch[i]);
		}
		//判断当前识别字符的位置，若没有识别完，则继续下一次识别
		while (temp < length) {
			getChar(temp);
		} 
		
	}
	
	//识别字符 传入当前识别字符的位置
	static void getChar(int i){
		int row;
		//识别关键字
		if(Character.isLetter(qCharacters.get(i))){
			StringBuilder strTemp = new StringBuilder();
			do {
				row = i;
				strTemp.append(qCharacters.get(i));
				i++;
			} while (Character.isLetter(qCharacters.get(i)));
				String str = String.valueOf(strTemp);
				searchK(str,row);
		}
		//识别常数及常数后跟一个字母的错误表达
		if(Character.isDigit(qCharacters.get(i))){
			StringBuilder strTemp = new StringBuilder();
			int b = 0;
			do {
				row = i;
				b = b * 10 + Character.digit(qCharacters.get(i), 10);
				strTemp.append(b);
				i++;
			} while (Character.isDigit(qCharacters.get(i)));
			if (Character.isLetter(qCharacters.get(i))) {
				row = i;
				strTemp.append(qCharacters.get(i));
				errorHandle(strTemp.toString(),row);
				i++;
			}else {
				searchCi(b,row);
			}
		}
		//识别分界符
		if (!Character.isLetterOrDigit(qCharacters.get(i))) {
			StringBuilder strTemp = new StringBuilder();
			do {
				if (isS(qCharacters.get(i))) {
					row = i;
					if (qCharacters.get(i) == '+' && qCharacters.get(i+1) == '+') {
						i++;
						searchS("++",row);
						i++;
						break;
					}
					if (qCharacters.get(i) == '-' && qCharacters.get(i+1) == '-') {
						i++;
						searchS("--",row);
						i++;
						break;
					}
					if (qCharacters.get(i) == '=' && qCharacters.get(i+1) == '=') {
						i++;
						searchS("==",row);
						i++;
						break;
					}
					searchS(String.valueOf(qCharacters.get(i)),row);
					i++;
//					需要在判断当前字符是否为分界符
				}else if (qCharacters.get(i) == '\n') {
					line++;
					i++;
					temp = i;
					return;
				}else{
					i++;
					temp = i;
					break;
				}
//				indexoutoferror
				if (temp == qCharacters.size()-1) {
						if (outtoken.size()>0) {
							System.out.println( "单词" + "                        " + "二元序列"  + "                    " +"类型"+ "             "+"位置(行,列)");
							for (int j = 0; j < outtoken.size(); j++) {
								System.out.println(outtoken.get(j).toString());
							}
					}
					break;
				}
			} while (!Character.isLetterOrDigit(qCharacters.get(i)));
		}
			temp = i;
	}
	
	
	static int readFile(File file){
		int readInfo;
		int len = 0;
		try {
			Reader reader = new FileReader(file);
			while ((readInfo = reader.read()) != -1){
				ch[len] = (char)readInfo;
				len++;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return len;
	}

	static void initList(){
		//初始化输出缓存表
		outtoken = new ArrayList<outputStr>();
		//初始化标识符表
		id = new ArrayList<String>();
		//初始化标识符表
		s = new HashMap<String,String>();
		//初始常数    表 
		ci = new ArrayList<Integer>();
		//初始关键字表 
//需改善成动态添加
		k = new ArrayList<String>();
		//添加关键字
		k.add("do");
		k.add("end");
		k.add("for");
		k.add("if");
		k.add("printf");
		k.add("scanf");
		k.add("then");
		k.add("while");
		
		//添加分界符
		s.put("0", "," );
		s.put("1", ";" );
		s.put("2", "(" );
		s.put("3", ")" );
		s.put("4", "[" );
		s.put("5", "]" );
		
		//添加算术运算符
		s.put("10H", "+" );
		s.put("11H", "-" );
		s.put("20H", "*" );
		s.put("21H", "/" );

		//添加关系运算符
		s.put("00H", "<" );
		s.put("01H", "<=" );
		s.put("02H", "=" );
		s.put("03H", ">" );
		s.put("04H", ">=" );
		s.put("05H", "<>" );
		
		for(int i = 0; i < 10; i++){
			ci.add(i);
		}
	}
		
	
	/*首先在关键字表中查找该字符串
	 * 查到return该字符串的位置
	 * 不在返回-1去查标识符表
	 */
	static int searchK(String strSearch,int row){
		int searchResult = k.indexOf(strSearch);
		if(searchResult >= 0){
			//输出(k,pointer)
			outtoken.add(new outputStr("1", strSearch, "关键字",row,line));
		}else {
			//查找标识符
			searchId(strSearch,row);
		}
		return searchResult;
	}
	
	
	/*首先标识符表中查找该字符串
	 * 查到return该字符串的位置
	 * 不在则添加至表中
	 */
	static void searchId(String strSearch,int row){
		int searchResult = id.indexOf(strSearch);
		if (searchResult >= 0) {
			outtoken.add(new outputStr("6", strSearch, "标识符",row,line));
		}else{
			id.add(strSearch);
			outtoken.add(new outputStr("6", strSearch, "标识符",row,line));
		}
	}
	
	/*
	 *查找分界符
	 *查到有注解则消除注解后输出
	 *否则直接输出 	
	 */
	static boolean isS(char ch){
		return s.containsValue(String.valueOf(ch));
	}
	
	static void searchS(String strSearch,int row){
		boolean searchResult = s.containsValue(strSearch);
		if (searchResult) {
			outtoken.add(new outputStr("2", strSearch, "分界符",row,line));
		}else{
			if (strSearch.trim().length()!=0) {
				outtoken.add(new outputStr("ERROR", strSearch, "ERROR",row,line));
			}else {
				return;
			}
		}
	}

	//处理错误字符
	static void errorHandle(String strError,int row){
		outtoken.add(new outputStr("ERROR", strError, "ERROR",row,line));
	}
	/*
	 *查找常数    
	 *查到则输出
	 *否则添加至表中后输出 	
	 */
	static void searchCi(Integer intSearch,int row){
		int searchResult = ci.indexOf(intSearch);
		if (searchResult >= 0) {
			outtoken.add(new outputStr("5", intSearch.toString(), "常数    ",row,line));
		}else {
			ci.add(intSearch);
			outtoken.add(new outputStr("5", intSearch.toString(), "常数    ",row,line));
		}
	}
	
	static boolean isLetter(Character ch){
		if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')){
			return true;
		}else {
			return false;
		}
	}
	
	static boolean isDigit(Character ch){
		if(ch >48 && ch < 57){
			return true;
		}else {
			return false;
		}
	}
		
}



/*
 * 输出类 
 * 包括单词本身 其二元序列， 类型 覆写了toString方法
 * 分析后的每个词的对应输出属性 方便保存
 */
class outputStr{
	String typeId;
	String pointer;
	String typeStr;
	int row;
	int line;

	
	public outputStr(String typeId, String pointer, String typeStr,int row,int line){
		this.typeId = typeId;
		this.pointer = pointer;
		this.typeStr = typeStr;
		this.row = row;
		this.line = line;
	}

//识别缓存输出格式
	public String toString() {
		if (typeStr.equals("ERROR")) {
			return pointer + "          " +  typeId  + "        " +typeStr + "     (" +  row  + "," +line+")";
		}else if (pointer.equals("for")||pointer.equals("end")) {
			return pointer + "         " + "(" + typeId + "," + pointer + ")"  + "      " +typeStr+ "              (" +  row  + "," +line+")";
		}else if (pointer.equals("then")) {
			return pointer + "        " + "(" + typeId + "," + pointer + ")"  + "     " +typeStr+ "              (" +  row  + "," +line+")";
		}else if (pointer.equals("while")||pointer.equals("scanf")) {
			return pointer + "        " + "(" + typeId + "," + pointer + ")"  + "        " +typeStr+ "              (" +  row  + "," +line+")";
		} 
		else
		return pointer + "           " + "(" + typeId + "," + pointer + ")"  + "        " +typeStr+ "              (" +  row  + "," +line+")";
	}
}
