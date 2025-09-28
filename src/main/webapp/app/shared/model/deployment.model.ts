import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IDeliverable } from 'app/shared/model/deliverable.model';
import { DeploymentStatus } from 'app/shared/model/enumerations/deployment-status.model';

export interface IDeployment {
  id?: number;
  deploymentDate?: dayjs.Dayjs | null;
  status?: keyof typeof DeploymentStatus | null;
  logs?: string | null;
  addendum?: string | null;
  deployedBy?: IUser | null;
  deliverable?: IDeliverable | null;
}

export const defaultValue: Readonly<IDeployment> = {};
