import { Injectable } from '@angular/core';
import { HttpClient, HttpParams  } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../../auth/models/user.model';

import { PageResponse } from '../../../shared/models/page.model';


@Injectable({
  providedIn: 'root'
})
export class UserAdminService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  // Récupérer tous les utilisateurs (ADMIN)
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  // Récupérer les utilisateurs paginés (ADMIN)
  getUsersPaged(page: number, size: number): Observable<PageResponse<User>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<PageResponse<User>>(`${this.apiUrl}/paged`, { params });
  }

  // Supprimer un utilisateur (ADMIN)
  deleteUser(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
}
