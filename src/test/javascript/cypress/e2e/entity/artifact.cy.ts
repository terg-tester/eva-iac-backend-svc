import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Artifact e2e test', () => {
  const artifactPageUrl = '/artifact';
  const artifactPageUrlPattern = new RegExp('/artifact(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const artifactSample = { name: 'drat brr' };

  let artifact;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/artifacts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/artifacts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/artifacts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (artifact) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/artifacts/${artifact.id}`,
      }).then(() => {
        artifact = undefined;
      });
    }
  });

  it('Artifacts menu should load Artifacts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('artifact');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Artifact').should('exist');
    cy.url().should('match', artifactPageUrlPattern);
  });

  describe('Artifact page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(artifactPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Artifact page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/artifact/new$'));
        cy.getEntityCreateUpdateHeading('Artifact');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', artifactPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/artifacts',
          body: artifactSample,
        }).then(({ body }) => {
          artifact = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/artifacts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/artifacts?page=0&size=20>; rel="last",<http://localhost/api/artifacts?page=0&size=20>; rel="first"',
              },
              body: [artifact],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(artifactPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Artifact page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('artifact');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', artifactPageUrlPattern);
      });

      it('edit button click should load edit Artifact page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Artifact');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', artifactPageUrlPattern);
      });

      it('edit button click should load edit Artifact page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Artifact');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', artifactPageUrlPattern);
      });

      it('last delete button click should delete instance of Artifact', () => {
        cy.intercept('GET', '/api/artifacts/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('artifact').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', artifactPageUrlPattern);

        artifact = undefined;
      });
    });
  });

  describe('new Artifact page', () => {
    beforeEach(() => {
      cy.visit(`${artifactPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Artifact');
    });

    it('should create an instance of Artifact', () => {
      cy.get(`[data-cy="name"]`).type('although');
      cy.get(`[data-cy="name"]`).should('have.value', 'although');

      cy.get(`[data-cy="description"]`).type('smoke gummy');
      cy.get(`[data-cy="description"]`).should('have.value', 'smoke gummy');

      cy.get(`[data-cy="type"]`).select('DIAGRAM');

      cy.get(`[data-cy="link"]`).type('which brr');
      cy.get(`[data-cy="link"]`).should('have.value', 'which brr');

      cy.get(`[data-cy="status"]`).select('FINAL');

      cy.get(`[data-cy="fileSize"]`).type('12145');
      cy.get(`[data-cy="fileSize"]`).should('have.value', '12145');

      cy.get(`[data-cy="createdDate"]`).type('2025-09-13T20:39');
      cy.get(`[data-cy="createdDate"]`).blur();
      cy.get(`[data-cy="createdDate"]`).should('have.value', '2025-09-13T20:39');

      cy.get(`[data-cy="lastModifiedDate"]`).type('2025-09-14T01:21');
      cy.get(`[data-cy="lastModifiedDate"]`).blur();
      cy.get(`[data-cy="lastModifiedDate"]`).should('have.value', '2025-09-14T01:21');

      cy.get(`[data-cy="addendum"]`).type('lest solemnly');
      cy.get(`[data-cy="addendum"]`).should('have.value', 'lest solemnly');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        artifact = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', artifactPageUrlPattern);
    });
  });
});
