import { TypeNotification } from '../enums/type-notification.enum';

export interface NotificationResponse {
  id: number;
  destinataireId: number;
  message: string;
  type: TypeNotification;
  lue: boolean;
  envoyeLe: string;
}
