package org.juefan.alg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LFM {
	
	public static int latent = 50;
	public static double alpha = 0.02;
	public static double lambda = 0.01;
	public static int iteration = 1;
	public static int resys = 10;
	
	public static Map<Integer, List<Float>> UserMap = new HashMap<Integer, List<Float>>();
	public static Map<Integer, List<Float>> ItemMap = new HashMap<Integer, List<Float>>();
		
	public static compares compare = new compares();
	
	public  class State{
		public int TemID;
		public Set<Integer> set = new HashSet<Integer>();
		public float sim;

		/**�û�������*/
		public State(Set<Integer> s, float s2){
			set.addAll(s);
			sim = s2;
		}

		/**Item����*/
		public State(Integer i, float s){
			TemID = i;
			sim = s;
		}
	}

	public static class compares implements Comparator<Object>{
		@Override
		public int compare(Object o1, Object o2) {			
			State s1 = (State)o1;
			State s2 = (State)o2;
			return s1.sim < s2.sim ? 1:0;
		}		
	}
	
	public  String toString(){
		return "FLM";
	}
	public LFM(Set<Integer> user, Set<Integer> item){
		for(Integer u:user){
			List<Float> tList = new ArrayList<Float>();
			for(int i = 0; i < latent; i++)
				tList.add((float) Math.random());
			UserMap.put(u, tList);
		}
		for(Integer u:item){
			List<Float> tList = new ArrayList<Float>();
			for(int i = 0; i < latent; i++)
				tList.add((float) Math.random());
			ItemMap.put(u, tList);
		}
	}
	public LFM() {
		// TODO Auto-generated constructor stub
	}
	/**
	 *  �����û���ĳ����Ʒ����Ȥ
	 * @param uLV	�û���������Ĺ�ϵ
	 * @param iLV	����������Ʒ�Ĺ�ϵ
	 * @return �����û���ĳ����Ʒ����Ȥ
	 */
	public static float getPreference(List<Float> uLV, List<Float> iLV){
		float p = 0;
		for(int i = 0; i < latent; i++){
			p = p + uLV.get(i) * iLV.get(i);
		}
		return p;
	}
	
	public static float Predict(float i1, float i2){
		return i1 - i2;
	}
	
	/**
	 * �������������
	 * @param UserItem	
	 */
	public static void LatentFactorModel(Map<Integer, Map<Integer, Float>> UserItem){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm");//�������ڸ�ʽ
		for(int i = 0; i < iteration; i++){	
			System.out.println( df.format(new Date()) + "\t�� " + (i + 1) + " �ε���");
			for(int user: UserItem.keySet()){
				for(int item: UserItem.get(user).keySet()){
					float error = Predict(UserItem.get(user).get(item), 
							getPreference(UserMap.get(user), ItemMap.get(item)));
					for(int i1 = 0; i1 < latent; i1++){
						UserMap.get(user).set(i1, (float) (UserMap.get(user).get(i1) + alpha * 
								(ItemMap.get(item).get(i1) * error - lambda * UserMap.get(user).get(i1))));
						
						ItemMap.get(item).set(i1, (float) (ItemMap.get(item).get(i1) + alpha * 
								(UserMap.get(user).get(i1) * error - lambda * ItemMap.get(item).get(i1))));
					}
				}
			}
			alpha = (float) (alpha * 0.99);
		}	
	}
	
	
	/**
	 * ��ȡ�û��������Ƽ��б�
	 * @param map ��Ŀ�ĵ÷�ֵ��
	 * @return
	 */
	public Set<Integer> getResysK(Map<Integer, Float> map){
		List<State> tList = new ArrayList<State>();
		Set<Integer> set = new HashSet<Integer>();
		for(Integer key: map.keySet())
			tList.add(new State(key,  map.get(key)));
		Collections.sort(tList, compare);

		for(int i = 0; i < tList.size() && i < resys; i++){
			set.add(tList.get(i).TemID);	
		}
		return set;
	}
	
	/**
	 *  �����û����Ƽ��б�
	 * @param user	�û���ID
	 * @param item	�û���ѵ����
	 * @return	�û����Ƽ��б�
	 */
	public Set<Integer> getResysList(int user, Map<Integer, Float> item){
		Map<Integer, Float> map = new HashMap<Integer, Float>();
		for(int i: ItemMap.keySet()){
			if(!item.containsKey(i))
				map.put(i, getPreference(UserMap.get(user), ItemMap.get(i)));
		}
		return getResysK(map);
	}
	

}