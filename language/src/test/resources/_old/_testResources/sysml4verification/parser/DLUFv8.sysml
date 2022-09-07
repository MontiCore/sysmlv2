/**
 * [CONFIDENTIAL] Handle with care
 * Data Link Upload Feed - A Case Study
 */
package dluf {
  /*
   * Datatypes
   */
  attribute def Packet {
    value size: Integer;
  }

  enum def CtrlEnum {
    enum Confirmation;
  }

  /*
   * Port Definitions
   */
  port def Packets {
    out data: Packet;
  }

  port def Ctrl {
    out data: CtrlEnum;
  }

  /*
   * Atomic Part Definition Buffer, Capacity, Merge
   */
  part def Buffer {
    port input: ~Packets;
    port control: ~Ctrl;
    port output: Packets;

    state def BufferAutomaton(in i: Packet, in s: CtrlEnum, out o: Packet) {
      entry; then S;
      state S;
      value b: List<Packet>;

      transition
        first S
        accept i
        if b.length() == 0
        do action { assign b := b.append(i); send i to o; }
        then S;

      transition
        first S
        accept i
        if b.length() > 0
        do assign b := b.append(i)
        then S;

      transition
        first S
        accept s
        if b.length() >= 2
        do action { assign b := b.tail(); send b.tail().head() to o; }
        then S;

      transition
        first S
        accept s
        if b.length() <= 1
        do assign b := b.tail()
        then S;

      transition
        first S
        accept Tick
        if b.length() > 0
        do send b.head() to o
        then S;
    }
    state ba: BufferAutomaton(i=input::data, s=control::data, o=output::data);
    exhibit ba;
  }

  part def Capacity {
    port input: ~Packets;
    port output: Packets;
    port control: Ctrl;

    final attribute cap: nat;

    state def CapacityAutomaton(in i: Packet, out o: Packet, out s: CtrlEnum) {
      entry action {
        assign c := cap;
      }
      then S;
      state S;
      value c: nat;

      transition
        first S
        accept i
        if c >= i.size()
        do action { send Confirmation to s; send i to o; assign c := c-i.size(); }
        then S;

      transition
        first S
        accept i
        if c < i.size()
        then S;

      transition
        first S
        accept Tick
        do assign c := cap
        then S;
    }
    state ca: CapacityAutomaton(i=input::data, o=output::data, s=control::data, cap=cap);
    exhibit ca;

  }

  part def Merge {
    port input1: ~Packets;
    port input2: ~Packets;
    port input3: ~Packets;
    port input4: ~Packets;
    port output: Packets;

    state def MergeAutomaton(
        in input1: Packet,
        in input2: Packet,
        in input3: Packet,
        in input4: Packet,
        out output: Packet)
    {
      entry; then S;
      state S;

      transition
        first S
        accept input1
        do send input1 to output
        then S;

      transition
        first S
        accept input2
        do send input2 to output
        then S;

      transition
        first S
        accept input3
        do send input3 to output
        then S;

      transition
        first S
        accept input4
        do send input4 to output
        then S;
    }
    state ma: MergeAutomaton(input1 = input1::data, input2 = input2::data, input3 = input3::data, input4 = input4::data,
        output = output::data);
    exhibit ma;
  }

  /*
   * Compositional Part Definitions Buffering, CapacityScheduler, DLUF
   */
  part def Buffering {
    port input1: ~Packets;
    port input2: ~Packets;
    port input3: ~Packets;
    port input4: ~Packets;
    port control1: ~Ctrl;
    port control2: ~Ctrl;
    port control3: ~Ctrl;
    port control4: ~Ctrl;
    port output1: Packets;
    port output2: Packets;
    port output3: Packets;
    port output4: Packets;

    part buffer1: Buffer;
    part buffer2: Buffer;
    part buffer3: Buffer;
    part buffer4: Buffer;

    connect input1 to buffer1.input;
    connect input2 to buffer2.input;
    connect input3 to buffer3.input;
    connect input4 to buffer4.input;
    connect control1 to buffer1.control;
    connect control2 to buffer2.control;
    connect control3 to buffer3.control;
    connect control4 to buffer4.control;
    connect buffer1.output to output1;
    connect buffer2.output to output2;
    connect buffer3.output to output3;
    connect buffer4.output to output4;
  }

  part def CapacityScheduler {
    port input1: ~Packets;
    port input2: ~Packets;
    port input3: ~Packets;
    port input4: ~Packets;
    port output1: Packets;
    port output2: Packets;
    port output3: Packets;
    port output4: Packets;
    port control1: Ctrl;
    port control2: Ctrl;
    port control3: Ctrl;
    port control4: Ctrl;

    part capacity1: Capacity { attribute cap = 20; } // TODO (20);
    part capacity2: Capacity { attribute cap = 15; } // TODO (15);
    part capacity3: Capacity { attribute cap = 10; } // TODO (10);
    part capacity4: Capacity { attribute cap = 5; } // TODO (5);

    connect input1 to capacity1.input;
    connect input2 to capacity2.input;
    connect input3 to capacity3.input;
    connect input4 to capacity4.input;
    connect capacity1.control to control1;
    connect capacity2.control to control2;
    connect capacity3.control to control3;
    connect capacity4.control to control4;
    connect capacity1.output to output1;
    connect capacity2.output to output2;
    connect capacity3.output to output3;
    connect capacity4.output to output4;
  }

  part def DLUF {
    port user1: ~Packets;
    port user2: ~Packets;
    port user3: ~Packets;
    port user4: ~Packets;
    port link: Packets;

    part buffers: Buffering;
    part scheduler: CapacityScheduler;
    part mergex: Merge;

    /*
     * _ -> BUFFER
     */
    connect user1 to buffers.input1;
    connect user2 to buffers.input2;
    connect user3 to buffers.input3;
    connect user4 to buffers.input4;

    /*
     * BUFFER <-> SCHEDULER
     */
    connect buffers.output1 to scheduler.input1;
    connect buffers.output2 to scheduler.input2;
    connect buffers.output3 to scheduler.input3;
    connect buffers.output4 to scheduler.input4;

    connect scheduler.control1 to buffers.control1;
    connect scheduler.control2 to buffers.control2;
    connect scheduler.control3 to buffers.control3;
    connect scheduler.control4 to buffers.control4;

    /*
     * SCHEDULER <-> MERGE
     */
    connect scheduler.output1 to mergex.input1;
    connect scheduler.output2 to mergex.input2;
    connect scheduler.output3 to mergex.input3;
    connect scheduler.output4 to mergex.input4;

    /*
     * MERGE -> _
     */
    connect mergex.output to link;
  }

  /*
  requirement def AlwaysTransmit {
    subject buffer: Buffer;
    assume constraint { forall t. buffer.input.get(t) != {} }
    assume constraint { forall t. buffer.control.get(t) != {} }
    require constraint { buffer.output.messageLength() == INF }
  }
  */

  part def BufferWithCapacity {
    port inp: ~Packets;
    port outp: Packets;
    attribute maxCap: nat;

    satisfy requirement nonStarvation {
      assume constraint InfLongTimeframe {
        forall i in {1,2,3,4}: inp[i].length() = INF
      }
      assume constraint messageInEachInterval {
        forall i in {1,2,3,4}, nat t:
          inp[i].atTime(t).length() > 0
      }
      assume constraint sizeBelowMaxCap {
        forall i in {1,2,3,4}:
          forall v in inp[i].values():
            v < maxCap[i]
      }
      require constraint infManyOutputMessages {
        forall i in {1,2,3,4}:
          outp[i].toUntimed().length() = INF
      }
    }
  }
}
