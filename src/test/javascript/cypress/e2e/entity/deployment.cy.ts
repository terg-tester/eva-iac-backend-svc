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

describe('Deployment e2e test', () => {
  const deploymentPageUrl = '/deployment';
  const deploymentPageUrlPattern = new RegExp('/deployment(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const deploymentSample = {};

  let deployment;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/deployments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/deployments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/deployments/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (deployment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/deployments/${deployment.id}`,
      }).then(() => {
        deployment = undefined;
      });
    }
  });

  it('Deployments menu should load Deployments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('deployment');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Deployment').should('exist');
    cy.url().should('match', deploymentPageUrlPattern);
  });

  describe('Deployment page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(deploymentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Deployment page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/deployment/new$'));
        cy.getEntityCreateUpdateHeading('Deployment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deploymentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/deployments',
          body: deploymentSample,
        }).then(({ body }) => {
          deployment = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/deployments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/deployments?page=0&size=20>; rel="last",<http://localhost/api/deployments?page=0&size=20>; rel="first"',
              },
              body: [deployment],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(deploymentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Deployment page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('deployment');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deploymentPageUrlPattern);
      });

      it('edit button click should load edit Deployment page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Deployment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deploymentPageUrlPattern);
      });

      it('edit button click should load edit Deployment page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Deployment');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deploymentPageUrlPattern);
      });

      it('last delete button click should delete instance of Deployment', () => {
        cy.intercept('GET', '/api/deployments/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('deployment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deploymentPageUrlPattern);

        deployment = undefined;
      });
    });
  });

  describe('new Deployment page', () => {
    beforeEach(() => {
      cy.visit(`${deploymentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Deployment');
    });

    it('should create an instance of Deployment', () => {
      cy.get(`[data-cy="deploymentDate"]`).type('2025-09-14T02:06');
      cy.get(`[data-cy="deploymentDate"]`).blur();
      cy.get(`[data-cy="deploymentDate"]`).should('have.value', '2025-09-14T02:06');

      cy.get(`[data-cy="status"]`).select('SUCCESS');

      cy.get(`[data-cy="logs"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="logs"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="addendum"]`).type('coincide sensitize');
      cy.get(`[data-cy="addendum"]`).should('have.value', 'coincide sensitize');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        deployment = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', deploymentPageUrlPattern);
    });
  });
});
