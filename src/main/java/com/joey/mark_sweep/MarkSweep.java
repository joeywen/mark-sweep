package com.joey.mark_sweep;
public class MarkSweep {

  private final static int STACK_MAX = 256;

  static enum ObjectType {
    OBJ_INT, OBJ_PAIR
  }

  class sObject {
    ObjectType type;
    boolean marked;

    // The next object in the linked list of heap allocated objects
    sObject next;

    int value;
    sObject head;
    sObject tail;
  }

  class VM {
    sObject stakc[];
    int stackSize;

    sObject firstObject;

    int numObjects;
    int maxObjects;

    public VM(sObject[] stakc, int stackSize, sObject firstObject,
        int numObjects, int maxObjects) {
      this.stakc = stakc;
      this.stackSize = stackSize;
      this.firstObject = firstObject;
      this.numObjects = numObjects;
      this.maxObjects = maxObjects;
    }

    public VM() {
      this.stakc = new sObject[STACK_MAX];
      this.stackSize = 0;
      this.firstObject = null;
      this.numObjects = 0;
      this.maxObjects = 8;
    }

  }

  public VM newVM() {
    return new VM();
  }

  public void push(VM vm, sObject value) {
    assert vm.stackSize < STACK_MAX;
    vm.stakc[vm.stackSize++] = value;
  }

  public sObject pop(VM vm) {
    assert vm.stackSize > 0;
    return vm.stakc[--vm.stackSize];
  }

  public void mark(sObject object) {
    /*
     * if already marked, we're done. Check this first to avoid recursing on
     * values in the object graph
     */
    if (object.marked)
      return;

    object.marked = true;
    if (object.type == ObjectType.OBJ_PAIR) {
      mark(object.head);
      mark(object.tail);
    }
  }

  public void markAll(VM vm) {
    for (int i = 0; i < vm.stackSize; ++i) {
      mark(vm.stakc[i]);
    }
  }

  public void sweep(VM vm) {
    sObject object = vm.firstObject;
    while (object != null) {
      if (!object.marked) {
        sObject unreached = object;
        object = unreached.next;
        free(unreached);

        vm.numObjects--;
      } else {
        object.marked = false;
        object = object.next;
      }
    }
  }

  private void free(sObject unreached) {
    unreached = null;
  }

  public void gc(VM vm) {
    int numObjects = vm.numObjects;

    markAll(vm);
    sweep(vm);

    vm.maxObjects = vm.numObjects * 2;

    System.out.println("Collected " + (numObjects - vm.numObjects)
        + " object, " + vm.numObjects + " remaining.");
  }
  
  public sObject newObject(VM vm, ObjectType type) {
    if (vm.numObjects == vm.maxObjects) gc(vm);
    sObject object = new sObject();
    object.type = type;
    object.next = vm.firstObject;
    vm.firstObject = object;
    object.marked = false;
    
    vm.numObjects ++;
    
    return object;
  }
  
  public void pushInt(VM vm, int intVal) {
    sObject object = newObject(vm, ObjectType.OBJ_INT);
    object.value = intVal;
    push(vm, object);
  }
  
  public sObject pushPair(VM vm) {
    sObject object = newObject(vm, ObjectType.OBJ_PAIR);
    object.tail = pop(vm);
    object.head = pop(vm);
    
    push(vm, object);
    
    return object;
  }
  
  public void dump(sObject object) {
    switch(object.type) {
    case OBJ_INT:
      System.out.println(object.value);
      break;
    case OBJ_PAIR:
      System.out.print("(");
      dump(object.head);
      System.out.print(", ");
      dump(object.tail);
      System.out.println(")");
      break;
    }
  }
}
