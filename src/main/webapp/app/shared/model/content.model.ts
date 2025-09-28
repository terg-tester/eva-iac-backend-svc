import dayjs from 'dayjs';

export interface IContent {
  id?: string;
  fileName?: string;
  dateCreated?: dayjs.Dayjs;
  contentContentType?: string;
  content?: string;
}

export const defaultValue: Readonly<IContent> = {};
