import React from 'react';
import { Translate } from 'react-jhipster'; // eslint-disable-line

import MenuItem from 'app/shared/layout/menus/menu-item'; // eslint-disable-line

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/project">
        <Translate contentKey="global.menu.entities.project" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/artifact">
        <Translate contentKey="global.menu.entities.artifact" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/deliverable">
        <Translate contentKey="global.menu.entities.deliverable" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/deployment">
        <Translate contentKey="global.menu.entities.deployment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/content">
        <Translate contentKey="global.menu.entities.content" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
