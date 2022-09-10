package com.dorohedoro.util;

public class IDGenerator {
        private final static long START_STMP = 1480166465631L;
        
        private final static long SEQUENCE_BIT = 12; 
        private final static long MACHINE_BIT = 5;   
        private final static long DATACENTER_BIT = 5;
    
        private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
        private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
        private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
        
        private final static long MACHINE_LEFT = SEQUENCE_BIT;
        private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
        private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

        private static long datacenterId; 
        private static long machineId;    
        private static long sequence = 0L;
        private static long lastStamp = -1L;

        public IDGenerator(long datacenterId, long machineId) {
            if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
                throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
            }
            if (machineId > MAX_MACHINE_NUM || machineId < 0) {
                throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
            }
            this.datacenterId = datacenterId;
            this.machineId = machineId;
        }

        public synchronized static long nextId() {
            long currStamp = getNewStamp();
            if (currStamp < lastStamp) {
                throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
            }

            if (currStamp == lastStamp) {
                sequence = (sequence + 1) & MAX_SEQUENCE;
                if (sequence == 0L) {
                    currStamp = getNextMill();
                }
            } else {
                sequence = 0L;
            }

            lastStamp = currStamp;

            return (currStamp - START_STMP) << TIMESTMP_LEFT 
                    | datacenterId << DATACENTER_LEFT       
                    | machineId << MACHINE_LEFT             
                    | sequence;                             
        }

        private static long getNextMill() {
            long mill = getNewStamp();
            while (mill <= lastStamp) {
                mill = getNewStamp();
            }
            return mill;
        }

        private static long getNewStamp() {
            return System.currentTimeMillis();
        }
}
