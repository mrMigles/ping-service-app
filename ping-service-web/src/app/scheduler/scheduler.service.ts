import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/index';
import { Task } from './task';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class SchedulerService {

  private schedulerUrl = '/tasks/';

  constructor(private http: HttpClient) { }

  getTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.schedulerUrl);
  }

  addTask(task: Task) {
    return this.http.put<Task>(this.schedulerUrl, task, httpOptions);
  }
}
