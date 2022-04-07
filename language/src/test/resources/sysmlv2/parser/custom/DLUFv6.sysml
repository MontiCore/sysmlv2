/**
 * [CONFIDENTIAL] Handle with care
 */
package 'Data Link Upload Feed - A Case Study' {

  package 'Architecture Modeling' {
    attribute def Packet {
      value size: Integer;
    }

    port def Packets {
      out data: Packet;
    }

    port def Booleans {
      out data: Boolean;
    }

    enum def Ctrl {
      enum TBD1;
      enum TBD2;
    }

    part def Buffer {
      port input: ~Packets;
      port control: ~Ctrl;
      port output: Packets;
      port empty: Booleans;

      value queue: List<Packet>;
    }

    part def PriorityScheduler {
      /*
       * BUFFER SIDE
       */
      port priority1: ~Packets;
      port priority2: ~Packets;
      port priority3: ~Packets;
      port priority4: ~Packets;

      /* Ob noch Nachrichten da sind */
      port isEmpty1: ~Booleans;
      port isEmpty2: ~Booleans;
      port isEmpty3: ~Booleans;
      port isEmpty4: ~Booleans;

      /* TBD */
      port ctrl1: ~Ctrl;
      port ctrl2: ~Ctrl;
      port ctrl3: ~Ctrl;
      port ctrl4: ~Ctrl;

      /*
       * CAPACITY SIDE
       */
      port priority1: Packets;
      port priority2: Packets;
      port priority3: Packets;
      port priority4: Packets;

      port isExceeded1: ~Booleans;
      port isExceeded2: ~Booleans;
      port isExceeded3: ~Booleans;
      port isExceeded4: ~Booleans;

      /*
       * STATES
       */
      value ready1: Boolean = False;
      value ready2: Boolean = False;
      value ready3: Boolean = False;
      value ready4: Boolean = False;

      /* Ob für dieses Intervall die Nachricht nicht mehr ins Rest-Budget passt */
      value exceeded1: Boolean = False;
      value exceeded2: Boolean = False;
      value exceeded3: Boolean = False;
      value exceeded4: Boolean = False;

      /* Ob Buffer ein empty=True gemeldet hat */
      value empty1: Boolean = False;
      value empty2: Boolean = False;
      value empty3: Boolean = False;
      value empty4: Boolean = False;
    }

    part def CapacityControl {
      port priority1: ~Packets;
      port priority2: ~Packets;
      port priority3: ~Packets;
      port priority4: ~Packets;

      port isExceeded1: Booleans;
      port isExceeded2: Booleans;
      port isExceeded3: Booleans;
      port isExceeded4: Booleans;

      port output: Packets;

      value linkCapacity = 50;
      value remainingCapacity = linkCapacity;
    }

    part def DataLink {
      port input: ~Packets;
    }

    part Context {
      port user1: ~Packets;
      port user2: ~Packets;
      port user3: ~Packets;
      port user4: ~Packets;

      part buffer1: Buffer;
      part buffer2: Buffer;
      part buffer3: Buffer;
      part buffer4: Buffer;

      part scheduler: PriorityScheduler;

      part capacity: CapacityControl;

      part link: Datalink;

      /*
       * _ -> BUFFER
       */
      connect user1 to buffer1.input;
      connect user2 to buffer2.input;
      connect user3 to buffer3.input;
      connect user4 to buffer4.input;

      /*
       * BUFFER <-> SCHEDULER
       */
      connect buffer1.output to scheduler.priority1;
      connect buffer2.output to scheduler.priority2;
      connect buffer3.output to scheduler.priority3;
      connect buffer4.output to scheduler.priority4;

      connect buffer1.isEmpty to scheduler.isEmpty1;
      connect buffer2.isEmpty to scheduler.isEmpty2;
      connect buffer3.isEmpty to scheduler.isEmpty3;
      connect buffer4.isEmpty to scheduler.isEmpty4;

      connect scheduler.ctrl1 to buffer1.ctrl;
      connect scheduler.ctrl2 to buffer2.ctrl;
      connect scheduler.ctrl3 to buffer3.ctrl;
      connect scheduler.ctrl4 to buffer4.ctrl;

      /*
       * SCHEDULER <-> CAPACITY
       */
      connect scheduler.priority1 to capacity.priority1;
      connect scheduler.priority2 to capacity.priority2;
      connect scheduler.priority3 to capacity.priority3;
      connect scheduler.priority4 to capacity.priority4;

      connect capacity.isExceeded1 to scheduler.isExceeded1;
      connect capacity.isExceeded2 to scheduler.isExceeded2;
      connect capacity.isExceeded3 to scheduler.isExceeded3;
      connect capacity.isExceeded4 to scheduler.isExceeded4;

      /*
       * CAPACITY -> DATA LINK
       */
      connect capacity.output to link.input;
    }
  }
}
