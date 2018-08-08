import {Component, Input, OnInit} from '@angular/core';
import {SchedulerService} from './scheduler.service';
import {Task} from './task';

@Component({
  selector: 'app-scheduler',
  templateUrl: './scheduler.component.html',
  styleUrls: ['./scheduler.component.css']
})
export class SchedulerComponent implements OnInit {
  tasks: Task[];

  @Input() task: Task;

  constructor(private schedulerService: SchedulerService) { }

  ngOnInit(): void {
    this.task = new Task();
    this.getTasks();
  }

  getTasks(): void {
    this.schedulerService.getTasks()
      .subscribe(task => this.tasks = task);
  }

  addTask(): void {
    this.task.isActive = false;
    this.schedulerService.addTask(this.task)
      .subscribe(task => this.tasks.push(task));

    this.tasks.push(this.task);
    this.task = new Task();
  }
}
