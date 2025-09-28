import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IProject } from 'app/shared/model/project.model';
import { ArtifactType } from 'app/shared/model/enumerations/artifact-type.model';
import { ArtifactStatus } from 'app/shared/model/enumerations/artifact-status.model';

export interface IArtifact {
  id?: number;
  name?: string;
  description?: string | null;
  type?: keyof typeof ArtifactType | null;
  link?: string | null;
  status?: keyof typeof ArtifactStatus | null;
  fileSize?: number | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  addendum?: string | null;
  uploadedBy?: IUser | null;
  projects?: IProject[] | null;
}

export const defaultValue: Readonly<IArtifact> = {};
