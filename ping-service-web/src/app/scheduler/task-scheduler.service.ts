import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/index';
import { Task } from './task';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class TaskSchedulerService {

  private schedulerUrl = '/tasks/';

  constructor(private http: HttpClient) { }

  getTask(id: number): Observable<Task> {
    const url = `${this.schedulerUrl}/${id}`;
    return this.http.get<Task>(url);
  }

  getTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.schedulerUrl);
  }

  addTask(task: Task) {
    return this.http.put<Task>(this.schedulerUrl, task, httpOptions);
  }

  startTask(id: number) {
    const url = `${this.schedulerUrl}/${id}/start`;
    return this.http.post(url, {}, {responseType: 'text'});
  }

  stopTask(id: number) {
    const url = `${this.schedulerUrl}/${id}/stop`;
    return this.http.post(url, {}, {responseType: 'text'});
  }
}
