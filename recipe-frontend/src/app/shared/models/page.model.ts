export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;        // index de la page actuelle (0-based)
  size: number;          // taille de la page
}
