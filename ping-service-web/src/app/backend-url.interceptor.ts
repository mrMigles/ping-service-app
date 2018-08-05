import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs/index';
import { Inject, Injectable } from '@angular/core';

@Injectable()
export class BackendUrlInterceptor implements HttpInterceptor {

  /* constructor(@Inject('BASE_URL') private baseUrl: string) {} */

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const url = 'http://localhost:8080';
    req = req.clone({
      url: url + req.url,
      withCredentials: true
    });

    // req.headers.

    return next.handle(req);
  }
}
