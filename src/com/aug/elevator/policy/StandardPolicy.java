package com.aug.elevator.policy;

import com.aug.elevator.model.Elevator;
import com.aug.elevator.model.Seed;
import com.aug.elevator.model.Elevator.MoveStatus;

import java.util.ArrayList;

public class StandardPolicy extends ElevatorPolicy {

    @Override
    public void preHandleSeeds(Elevator elevator, int floor, 
            ArrayList<Seed> seedsList,
            int topFloor, int bottomFloor) {
        if (seedsList.size() == 0) {
            return;
        }
        if (elevator.isOverLoad()) {
            return;
        }

        
        int loadSpace = elevator.getLoadSpace();
        for (int i = 0; 0 < loadSpace && i < seedsList.size(); i++) {
            // stepCost 不能只计算绝对值，要计算方向。
            // 如果电梯向上走，要走到最上面有人的楼层，再向下
            int stepCost = 0;
            
            Seed seed = seedsList.get(i);
            elevator.setActive(floor, seed.getToFloor());
            
            boolean elevatorIdle = elevator.getMoveStatus() == MoveStatus.IDLE;
            boolean elevatorGoUp = elevator.getMoveStatus() == MoveStatus.UP || elevator.getMoveStatus() == MoveStatus.PRE_UP;
            boolean elevatorGoDown = elevator.getMoveStatus() == MoveStatus.DOWN || elevator.getMoveStatus() == MoveStatus.PRE_DOWN;
            
            boolean sameDir = elevatorIdle || (elevatorGoUp && !seed.isDown()) || (elevatorGoDown && seed.isDown());
            if (sameDir) {
                stepCost = Math.abs(elevator.getCurrentFloor() - floor);
            } else if (elevatorGoUp){
                stepCost = Math.abs(elevator.getCurrentFloor() - topFloor);
                stepCost += Math.abs(topFloor - floor);
            } else if (elevatorGoDown) {
                stepCost = Math.abs(elevator.getCurrentFloor() - bottomFloor);
                stepCost += Math.abs(bottomFloor - floor);
            }
            seed.setMarkElevatorId(elevator.getId(), stepCost);
            loadSpace--;
        }
    }

}
