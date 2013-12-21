package com.joey.mark_sweep;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.joey.mark_sweep.MarkSweep.VM;
import com.joey.mark_sweep.MarkSweep;
import com.joey.mark_sweep.MarkSweep.sObject;

public class TestMarkSweep {

  private MarkSweep markSweep = null;
  
  @Before
  public void setup() {
    markSweep = new MarkSweep();
  }
  
  @Test
  public void test1() {
    System.out.println("Test 1: Objects on stack are preserved.");
    VM vm = markSweep.newVM();
    
    markSweep.pushInt(vm, 1);
    markSweep.pushInt(vm, 2);
    
    markSweep.gc(vm);
    
    assertEquals("Should have preserved objects.", 2, vm.numObjects);
  }
  
  @Test
  public void test2() {
    System.out.println("Test 2: Unreached objects are collected.\n");
    VM vm = markSweep.newVM();
    markSweep.pushInt(vm, 1);
    markSweep.pushInt(vm, 2);
    markSweep.pop(vm);
    markSweep.pop(vm);

    markSweep.gc(vm);
    assertEquals("Should have collected objects.", 0, vm.numObjects);
  }
  
  @Test
  public void test3() {
    System.out.println("Test 3: Reach nested objects.\n");
    VM vm = markSweep.newVM();
    markSweep.pushInt(vm, 1);
    markSweep.pushInt(vm, 2);
    markSweep.pushPair(vm);
    markSweep.pushInt(vm, 3);
    markSweep.pushInt(vm, 4);
    markSweep.pushPair(vm);
    markSweep.pushPair(vm);

    markSweep.gc(vm);
    assertEquals("Should have reached objects.", 7, vm.numObjects);
  }
  
  @Test
  public void test4() {
    System.out.println("Test 4: Handle cycles.\n");
    VM vm = markSweep.newVM();
    markSweep.pushInt(vm, 1);
    markSweep.pushInt(vm, 2);
    sObject a = markSweep.pushPair(vm);
    markSweep.pushInt(vm, 3);
    markSweep.pushInt(vm, 4);
    sObject b = markSweep.pushPair(vm);

    a.tail = b;
    b.tail = a;

    markSweep.gc(vm);
    assertEquals("Should have collected objects.", 4, vm.numObjects);
  }
}
