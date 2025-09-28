import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IArtifact } from 'app/shared/model/artifact.model';
import { ProjectStatus } from 'app/shared/model/enumerations/project-status.model';
import { Priority } from 'app/shared/model/enumerations/priority.model';

export interface IProject {
  id?: number;
  name?: string;
  description?: string | null;
  status?: keyof typeof ProjectStatus | null;
  priority?: keyof typeof Priority | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  addendum?: string | null;
  createdBy?: IUser | null;
  artifacts?: IArtifact[] | null;
}

export const defaultValue: Readonly<IProject> = {};
