import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/categories">
        <Translate contentKey="global.menu.entities.categories" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/produits">
        <Translate contentKey="global.menu.entities.produits" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/clients">
        <Translate contentKey="global.menu.entities.clients" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/commandes">
        <Translate contentKey="global.menu.entities.commandes" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
