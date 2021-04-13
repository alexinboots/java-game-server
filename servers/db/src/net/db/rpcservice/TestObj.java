package net.db.rpcservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ckf
 */
public class TestObj implements Serializable {
	
	private int num1;

	private int num2;

	private int num3;
	
	private String str1;

	private String str2;

	private String str3;
	
	private Map<Integer, Integer> map = new HashMap<>();
	
	private List<String> strs = new ArrayList<>();
	
	public static TestObj valueOf(int num1, int num2, int num3, String str1, String str2, String str3) {
		TestObj t = new TestObj();
		t.num1 = num1;
		t.num2 = num2;
		t.num3 = num3;
		t.map.put(num1, num1);
		t.map.put(num2, num2);
		t.map.put(num3, num3);
		t.str1 = str1;
		t.str2 = str2;
		t.str3 = str3;
		t.strs.add(str1);
		t.strs.add(str2);
		t.strs.add(str3);
		return t;
	}

	public int getNum1() {
		return num1;
	}

	public void setNum1(int num1) {
		this.num1 = num1;
	}

	public int getNum2() {
		return num2;
	}

	public void setNum2(int num2) {
		this.num2 = num2;
	}

	public int getNum3() {
		return num3;
	}

	public void setNum3(int num3) {
		this.num3 = num3;
	}

	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public String getStr2() {
		return str2;
	}

	public void setStr2(String str2) {
		this.str2 = str2;
	}

	public String getStr3() {
		return str3;
	}

	public void setStr3(String str3) {
		this.str3 = str3;
	}

	public Map<Integer, Integer> getMap() {
		return map;
	}

	public void setMap(Map<Integer, Integer> map) {
		this.map = map;
	}

	public List<String> getStrs() {
		return strs;
	}

	public void setStrs(List<String> strs) {
		this.strs = strs;
	}
}
