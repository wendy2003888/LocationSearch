import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse} from '@angular/common/http';

import { Subject }    from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { ErrorObservable } from 'rxjs/observable/ErrorObservable';
import { catchError, retry } from 'rxjs/operators';



const httpOptions = {
    headers: new HttpHeaders({
      'Content-Type':  'application/json'
    })
};


@Injectable()
export class SearchService {

  IP_API_URL = 'http:\/\/ip-api.com/json';
  // HOST = 'http:\/\/localhost:3000/';
  // HOST = 'https:\/\/wendyh2003888.appspot.com/';
  HOST = 'http:\/\/nodeapp-666.us-east-2.elasticbeanstalk.com/';
  
  API = {
    getResult: 'gg/nearby?',
    getPage: 'gg/page?pagetoken=',
    getDetail: 'gg/detail?id=',
    getPhoto: 'gg/photo?reference=',
    getYelpBest: 'yelp/best?',
    getYelpReview: 'yelp/business?id='
    };

  

  private resultGotSource = new Subject<object>();
  resultGot$ = this.resultGotSource.asObservable();

  private detail: object;
  private detailSource = new Subject<object>();
  detailGot$ = this.detailSource.asObservable();

  private placeIdSource = new Subject<string>();  
  placeIdGot$ = this.placeIdSource.asObservable();

  private favorite = {};
  private favoriteSource = new Subject<object>();
  favoriteGot$ = this.favoriteSource.asObservable();
  myStorage: any;

  private myLocation: string;

  private itemChangeSource = new Subject<boolean>();
  itemChangeGot$ = this.itemChangeSource.asObservable();

  constructor(private http: HttpClient) { 
    this.myStorage = window.localStorage;

  }

  getIPLocate() {
    return this.http.get(this.IP_API_URL)
      .pipe(
        catchError(this.handleError)
      );
  }

  reqResult(dataForm) {
    var req_data = '';
    for (const [key, value] of Object.entries(dataForm)) {
      // console.log(key, value);
      if (req_data) {
        req_data += '&';
      }
      req_data += key + '=' + value;
    }
    return this.http.get(this.HOST + this.API.getResult + req_data)
      .pipe(
        catchError(this.handleError)
      );
  }

  sendResult(data: object) {
    this.resultGotSource.next(data);
  }

  reqPage(pagetoken) {
    return this.http.get(this.HOST + this.API.getPage + pagetoken)
      .pipe(
        catchError(this.handleError)
      );
  }

  reqDetail(id) {
    return this.http.get(this.HOST + this.API.getDetail + id)
      .pipe(
        catchError(this.handleError)
      );
  }

  sendDetail(data: object) {
    this.detail = data;
    this.detailSource.next(this.detail);
  }

  getDetail(): Observable<object> {
    return of(this.detail);
  }

  setMyLocaation(loc: string) {
    this.myLocation = loc;
  }

  getMyLocation(): Observable<string> {
    return of(this.myLocation);
  }

  sentItemChange(status: boolean) {
    this.itemChangeSource.next(status);
  }


  reqPhoto(reference) {
    return this.http.get(this.HOST + this.API.getPhoto + reference)
      .pipe(
        catchError(this.handleError)
      );
  }

  reqyelpMatch(req: object) {
    // console.log(req);
    var req_data = '';
    for (const [key, value] of Object.entries(req)) {
      if (req_data) {
        req_data += '&';
      }
      req_data += key + '=' + value;
    }
    return this.http.get(this.HOST + this.API.getYelpBest + req_data)
      .pipe(
        catchError(this.handleError)
      );
  }

  reqyelpReview(id: string) {
    return this.http.get(this.HOST + this.API.getYelpReview + id)
      .pipe(
        catchError(this.handleError)
      );
  }

  sendPlaceId(placeId: string) {
    this.placeIdSource.next(placeId);
  }

  addFavorite(item) {
    this.myStorage.setItem(item.place_id, JSON.stringify(item));
    var data = this.getAllStorage();
    this.favoriteSource.next(data);

  } 

  deleteFavorite(place_id: string) {
    this.myStorage.removeItem(place_id);
    var data = this.getAllStorage();
    this.favoriteSource.next(data);
  }

  getFavorites(): Observable<object>{
    var data = this.getAllStorage();
    return of(data);
  }

  getAllStorage() {
    var data = {};
    for (const key of Object.keys(this.myStorage)) {
      data[key] = JSON.parse(this.myStorage.getItem(key));
    }
    // console.log(data);
    return data;
  }

  private handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
    }
    // return an ErrorObservable with a user-facing error message
    return new ErrorObservable(
      'Something bad happened; please try again later.');
  };


}
