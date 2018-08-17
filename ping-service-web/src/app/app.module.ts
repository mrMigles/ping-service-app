import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { SchedulerComponent } from './scheduler/scheduler.component';
import { AppRoutingModule } from './app-routing.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TaskSchedulerService } from './scheduler/task-scheduler.service';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { BackendUrlInterceptor } from './backend-url.interceptor';
import { FormsModule } from '@angular/forms';
import {TaskComponent} from "./scheduler/task/task.component";
import {SlideToggleModule} from "ng2-slide-toggle";
import {TaskCardComponent} from "./scheduler/task-card/task-card.component";

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    SchedulerComponent,
    TaskComponent,
    TaskCardComponent
  ],
  imports: [
    BrowserModule,
    NgbModule.forRoot(),
    SlideToggleModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    TaskSchedulerService,
    { provide: HTTP_INTERCEPTORS, useClass: BackendUrlInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
