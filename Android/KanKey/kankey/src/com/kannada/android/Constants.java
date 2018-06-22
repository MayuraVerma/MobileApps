package com.kannada.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.KeyEvent;

public class Constants {
	

	public static final int ANUSWARA = 0;
	public static final int VOWELS = 1;
	public static final int CONSONANTS = 2;
	public static final int NOOFANUSWARA = 2;
	public static final int NOOFVOWELS = 18;
	public static final int NOOFCONSONANTS = 37;
	public static final int f = 0xCCD;
	public static final int r = 3248;
	
	public static final int F = 70;
	
	public static final int ZWNJ = 0x200C;
	public static final int ZWJ = 0x200D;
	
	public static final int SPACE = 32;
	public static final int TAB = '\t';
	
	public static Boolean isConsonant = false;
	public static Boolean processGunitakshara = false;
	
	public static  final Map<Integer, Integer> SHIFTED_KEYS = new HashMap<Integer, Integer>();
    static final Map<String,Integer> ANUSWARAVISARGA_MAP = new HashMap<String,Integer>();
    static final Map<Integer,Integer> VOWELS_MAP = new HashMap<Integer,Integer>();
    static final Map<String,Integer> CONSONAANTS_MAP = new HashMap<String,Integer>();
    
    static final List<Integer> ANUSWARAVISARGA_LIST = new ArrayList<Integer>();
    static final List<Integer> VOWELS_LIST = new ArrayList<Integer>();
    //List<Integer> VOWELS_OTTAKSHARA_LIST = new ArrayList<Integer>();
    static final List<Integer> CONSONAANTS_LIST = new ArrayList<Integer>();

   static{ 
	   
		SHIFTED_KEYS.put(2970, 3000);
		SHIFTED_KEYS.put(2985, 2979);
		SHIFTED_KEYS.put(2992, 2993);
		SHIFTED_KEYS.put(2994, 2995);
		
    	ANUSWARAVISARGA_LIST.add(0xC82);
    	ANUSWARAVISARGA_LIST.add(0xC83);
    	
    	VOWELS_LIST.add(0xC85);
    	VOWELS_LIST.add(0xC86);
    	VOWELS_LIST.add(0xC87);
    	VOWELS_LIST.add(0xC88);
    	VOWELS_LIST.add(0xC89);
    	VOWELS_LIST.add(0xC8A);
    	VOWELS_LIST.add(0xC8B);
    	VOWELS_LIST.add(0xC8E);
    	VOWELS_LIST.add(0xC8F);
    	VOWELS_LIST.add(0xC90);
    	VOWELS_LIST.add(0xC92);
    	VOWELS_LIST.add(0xC93);
    	VOWELS_LIST.add(0xC94);
    	VOWELS_LIST.add(0xCDE);
    	VOWELS_LIST.add(0xCDE);
    	
    	VOWELS_MAP.put(0xC85,-1);
    	VOWELS_MAP.put(0xC86,0xCBE);
    	VOWELS_MAP.put(0xC87,0xCBF);
    	VOWELS_MAP.put(0xC88,0xCC0);
    	VOWELS_MAP.put(0xC89,3265);
    	VOWELS_MAP.put(0xC8A,0XCC2);
    	VOWELS_MAP.put(0xC8B,0xCC3);
    	VOWELS_MAP.put(0xC8E,0xCC6);
    	VOWELS_MAP.put(0xC8F,0xCC7);
    	VOWELS_MAP.put(0xC90,0xCC8);
    	VOWELS_MAP.put(0xC92,0xCCA);
    	VOWELS_MAP.put(0xC93,0xCCB);
    	VOWELS_MAP.put(0xC94,0xCCC);
    	VOWELS_MAP.put(0xCDE,0xCC3);
    	VOWELS_MAP.put(0xCDE,0xCC4);
    
    	
		CONSONAANTS_LIST.add(0xC95);
		CONSONAANTS_LIST.add(0xC96);
		CONSONAANTS_LIST.add(0xC97);
		CONSONAANTS_LIST.add(0xC98);
		CONSONAANTS_LIST.add(0xC99);
		CONSONAANTS_LIST.add(0xC9A);
		CONSONAANTS_LIST.add(0xC9B);
		CONSONAANTS_LIST.add(0xC9C);
		CONSONAANTS_LIST.add(0xC9D);
		CONSONAANTS_LIST.add(0xC9E);
		CONSONAANTS_LIST.add(0xC9F);
		CONSONAANTS_LIST.add(0xCA0);
		CONSONAANTS_LIST.add(0xCA1);
		CONSONAANTS_LIST.add(0xCA2);
		CONSONAANTS_LIST.add(0xCA3);
		CONSONAANTS_LIST.add(0xCA4);
		CONSONAANTS_LIST.add(0xCA5);
		CONSONAANTS_LIST.add(0xCA6);
		CONSONAANTS_LIST.add(0xCA7);
		CONSONAANTS_LIST.add(0xCA8);
		CONSONAANTS_LIST.add(0xCAA);
		CONSONAANTS_LIST.add(0xCAB);
		CONSONAANTS_LIST.add(0xCAC);
		CONSONAANTS_LIST.add(0xCAD);
		CONSONAANTS_LIST.add(0xCAE);
		CONSONAANTS_LIST.add(0xCAF);
		CONSONAANTS_LIST.add(0xCB0);
		CONSONAANTS_LIST.add(0xCB2);
		CONSONAANTS_LIST.add(0xCB3);
		CONSONAANTS_LIST.add(0xCB5);
		CONSONAANTS_LIST.add(0xCB6);
		CONSONAANTS_LIST.add(0xCB7);
		CONSONAANTS_LIST.add(0xCB8);
		CONSONAANTS_LIST.add(0xCB9);
		CONSONAANTS_LIST.add(0xCCD);
		CONSONAANTS_LIST.add(0x0CB);
		CONSONAANTS_LIST.add(0x0CB);
	}
	
   public static int previous_0 = 0;
   public static int previous_1 = 0;
   public static int previous_2 = 0;
   public static int previous_3 = 0;
	
}
