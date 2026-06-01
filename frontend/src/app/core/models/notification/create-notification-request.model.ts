import { TypeNotification } from '../enums/type-notification.enum';

export interface CreateNotificationRequest {
  destinataireId: number;
  message: string;
  type: TypeNotification;
}
