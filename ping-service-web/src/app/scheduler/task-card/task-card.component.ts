import { Component, Input, OnInit } from '@angular/core';
import { Task } from '../task';
import { Location } from '@angular/common';
import { ActivatedRoute } from "@angular/router";
import { TaskSchedulerService } from "../task-scheduler.service";

@Component({
  selector: 'task-card',
  templateUrl: './task-card.component.html',
  styleUrls: ['./task-card.component.css']
})
export class TaskCardComponent implements OnInit {

  @Input() task: Task;

  constructor(
    private taskSchedulerService: TaskSchedulerService
  ) { }

  ngOnInit(): void {
  }

  onChanged(value:boolean){
    if (value) {
      this.taskSchedulerService.startTask(this.task.id)
        .subscribe(_ => this.task.isActive = true);
    } else {
      this.taskSchedulerService.stopTask(this.task.id)
        .subscribe(_ => this.task.isActive = false);
    }
  }
}
