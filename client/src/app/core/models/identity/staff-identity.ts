import {StaffMember} from './staff-member';
import {Location} from './location';

export interface StaffIdentity {
  location: Location;
  staffMember: StaffMember;
}
