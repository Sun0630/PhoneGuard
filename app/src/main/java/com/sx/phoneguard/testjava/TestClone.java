package com.sx.phoneguard.testjava;/**
 * Created by ad on 2016/3/19.
 * 测试克隆
 */
public class TestClone {
    public static void main(String[] args) {

        try {
            Dog dog = new Dog();
            Dog dog1 = (Dog) dog.clone();
            dog1.weight = 10;
            System.out.println(dog.weight);//2
            System.out.println(dog1.weight);//10
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }


}

class Dog extends BaseCloneClass {
    int weight = 2;
}

