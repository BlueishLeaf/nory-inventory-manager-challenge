import { HttpInterceptorFn } from '@angular/common/http';
import {StorageConstants} from '../constants/storage.constants';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Get the auth token from local storage
  const staff_id = localStorage.getItem(StorageConstants.STAFF_MEMBER_ID_KEY);
  const location_id = localStorage.getItem(StorageConstants.LOCATION_ID_KEY);

  // Only add an auth header if the token exists
  console.info("Checking headers...", staff_id, location_id)
  if (staff_id && location_id) {
    // Clone the request and add the authorization header
    const authReq = req.clone({
      headers: req.headers
        .set(StorageConstants.STAFF_MEMBER_ID_KEY, staff_id)
        .set(StorageConstants.LOCATION_ID_KEY, location_id)
    });

    // Send the newly created request
    return next(authReq);
  }

  // If no token exists, just forward the request unchanged
  return next(req);
};
