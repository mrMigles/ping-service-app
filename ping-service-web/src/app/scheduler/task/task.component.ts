import { Component, Input, OnInit } from '@angular/core';
import { Task } from '../task';
import { Location } from '@angular/common';
import { ActivatedRoute } from "@angular/router";
import { TaskSchedulerService } from "../task-scheduler.service";

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {
  tasks: Task[];

  @Input() task: Task;

  constructor(
    private route: ActivatedRoute,
    private taskSchedulerService: TaskSchedulerService,
    private location: Location
  ) { }

  ngOnInit(): void {
    this.loadTask();
  }

  loadTask(): void {
    const id = +this.route.snapshot.paramMap.get('id');
    this.taskSchedulerService.getTask(id)
      .subscribe(task => this.task = task);
  }

}
