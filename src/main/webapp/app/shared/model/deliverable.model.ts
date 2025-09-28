import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IProject } from 'app/shared/model/project.model';
import { DeliverableType } from 'app/shared/model/enumerations/deliverable-type.model';
import { DeliverableFormat } from 'app/shared/model/enumerations/deliverable-format.model';
import { DeploymentStatus } from 'app/shared/model/enumerations/deployment-status.model';

export interface IDeliverable {
  id?: number;
  name?: string;
  description?: string | null;
  type?: keyof typeof DeliverableType | null;
  format?: keyof typeof DeliverableFormat | null;
  status?: keyof typeof DeploymentStatus | null;
  packagePath?: string | null;
  packageSize?: number | null;
  checksum?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  addendum?: string | null;
  createdBy?: IUser | null;
  project?: IProject | null;
}

export const defaultValue: Readonly<IDeliverable> = {};
