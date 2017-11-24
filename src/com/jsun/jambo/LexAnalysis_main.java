package com.jsun.jambo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.*;




public class LexAnalysis_main {

	static List<String> k;//k���� �ؼ��ֱ�
	static Map<String,String> s; //s���� �ֽ����<�����ֽ��������������͹�ϵ�����>
	static List<String> id;//id���� ��ű�ʶ��
	static List<Integer> ci; //ci�����ų���    
	List<String> instring;//instring���� ����Ϊ����Դ����ĵ��ʻ���
	static List <outputStr> outtoken;// outtoken����   ��¼Ϊ����ڲ���ʾ����
	static char ch[] = new char[65530];//��¼��ȡ����ַ�
	char charTemp = ' ';
	static int temp = 0; //���嵱ǰʶ����ַ�λ��
	static int line = 1; //���嵱ǰ�У���ת�з���++
	static LinkedList<Character> qCharacters; //ʹ������ȡʶ����ַ�


	
	public static void main(String[] args) throws IOException {
		initList(); //��ʼ������
		String fileName = "F:\\PLSourceCode"+File.separator+""+"pltest.txt"; //��ȡԴ�ļ�λ��
		File file = new File(fileName);
		int length = readFile(file);
		qCharacters = new LinkedList<>();
		for (int i = 0; i < length; i++) {
				qCharacters.add(ch[i]);
		}
		//�жϵ�ǰʶ���ַ���λ�ã���û��ʶ���꣬�������һ��ʶ��
		while (temp < length) {
			getChar(temp);
		} 
		
	}
	
	//ʶ���ַ� ���뵱ǰʶ���ַ���λ��
	static void getChar(int i){
		int row;
		//ʶ��ؼ���
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
		//ʶ�������������һ����ĸ�Ĵ�����
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
		//ʶ��ֽ��
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
//					��Ҫ���жϵ�ǰ�ַ��Ƿ�Ϊ�ֽ��
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
							System.out.println( "����" + "                        " + "��Ԫ����"  + "                    " +"����"+ "             "+"λ��(��,��)");
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
		//��ʼ����������
		outtoken = new ArrayList<outputStr>();
		//��ʼ����ʶ����
		id = new ArrayList<String>();
		//��ʼ����ʶ����
		s = new HashMap<String,String>();
		//��ʼ����    �� 
		ci = new ArrayList<Integer>();
		//��ʼ�ؼ��ֱ� 
//����Ƴɶ�̬���
		k = new ArrayList<String>();
		//��ӹؼ���
		k.add("do");
		k.add("end");
		k.add("for");
		k.add("if");
		k.add("printf");
		k.add("scanf");
		k.add("then");
		k.add("while");
		
		//��ӷֽ��
		s.put("0", "," );
		s.put("1", ";" );
		s.put("2", "(" );
		s.put("3", ")" );
		s.put("4", "[" );
		s.put("5", "]" );
		
		//������������
		s.put("10H", "+" );
		s.put("11H", "-" );
		s.put("20H", "*" );
		s.put("21H", "/" );

		//��ӹ�ϵ�����
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
		
	
	/*�����ڹؼ��ֱ��в��Ҹ��ַ���
	 * �鵽return���ַ�����λ��
	 * ���ڷ���-1ȥ���ʶ����
	 */
	static int searchK(String strSearch,int row){
		int searchResult = k.indexOf(strSearch);
		if(searchResult >= 0){
			//���(k,pointer)
			outtoken.add(new outputStr("1", strSearch, "�ؼ���",row,line));
		}else {
			//���ұ�ʶ��
			searchId(strSearch,row);
		}
		return searchResult;
	}
	
	
	/*���ȱ�ʶ�����в��Ҹ��ַ���
	 * �鵽return���ַ�����λ��
	 * ���������������
	 */
	static void searchId(String strSearch,int row){
		int searchResult = id.indexOf(strSearch);
		if (searchResult >= 0) {
			outtoken.add(new outputStr("6", strSearch, "��ʶ��",row,line));
		}else{
			id.add(strSearch);
			outtoken.add(new outputStr("6", strSearch, "��ʶ��",row,line));
		}
	}
	
	/*
	 *���ҷֽ��
	 *�鵽��ע��������ע������
	 *����ֱ����� 	
	 */
	static boolean isS(char ch){
		return s.containsValue(String.valueOf(ch));
	}
	
	static void searchS(String strSearch,int row){
		boolean searchResult = s.containsValue(strSearch);
		if (searchResult) {
			outtoken.add(new outputStr("2", strSearch, "�ֽ��",row,line));
		}else{
			if (strSearch.trim().length()!=0) {
				outtoken.add(new outputStr("ERROR", strSearch, "ERROR",row,line));
			}else {
				return;
			}
		}
	}

	//��������ַ�
	static void errorHandle(String strError,int row){
		outtoken.add(new outputStr("ERROR", strError, "ERROR",row,line));
	}
	/*
	 *���ҳ���    
	 *�鵽�����
	 *������������к���� 	
	 */
	static void searchCi(Integer intSearch,int row){
		int searchResult = ci.indexOf(intSearch);
		if (searchResult >= 0) {
			outtoken.add(new outputStr("5", intSearch.toString(), "����    ",row,line));
		}else {
			ci.add(intSearch);
			outtoken.add(new outputStr("5", intSearch.toString(), "����    ",row,line));
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
 * ����� 
 * �������ʱ��� ���Ԫ���У� ���� ��д��toString����
 * �������ÿ���ʵĶ�Ӧ������� ���㱣��
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

//ʶ�𻺴������ʽ
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
