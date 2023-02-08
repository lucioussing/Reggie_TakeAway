package com.it.reggie_takeaway;

public class Test {
	public static void main(String[] args) {

			int[] arr=new int[]{7,11,4,3,2,0};
			int temp=0;
			for (int i=0;i<arr.length-1;i++){
				for (int j=0;j<arr.length-1-i;j++){
					if (arr[j]>arr[j+1]){
						temp=arr[j+1];
						arr[j+1]=arr[j];
						arr[j]=temp;

					}
				}
			}
		for (int i=0;i<arr.length;i++){
			System.out.println(arr[i]);
		}
		}


}
