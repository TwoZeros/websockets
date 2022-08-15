package com.example.websockets.services;

import com.example.websockets.services.interfaces.ISimpleNumberGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
@Service
public class SimpleNumberGenerator implements ISimpleNumberGenerator {
    static int var = 2;
    static final int MIN_VAL = 1;
    private Random random = new Random();
    @Override
    public List<Integer> generateNumbers(int maxValue, int countNumber) {
        List<Integer> integerList = new ArrayList<>();
        IntStream.range(0,countNumber).forEach( x -> {
            var num = random.nextInt(MIN_VAL,maxValue);
            while(!isPrime(num)) {
                num = random.nextInt(MIN_VAL,maxValue);
            }
                integerList.add(num);

        });
        return integerList;
    }

    //checks whether an int is prime or not.
    public boolean isPrime(int n) {
        //check if n is a multiple of 2
        if (n%2==0) return false;
        //if not, then just check the odds
        for(int i=3;i*i<=n;i+=2) {
            if(n%i==0)
                return false;
        }
        return true;
    }

}
