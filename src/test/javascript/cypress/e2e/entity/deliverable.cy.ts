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

describe('Deliverable e2e test', () => {
  const deliverablePageUrl = '/deliverable';
  const deliverablePageUrlPattern = new RegExp('/deliverable(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const deliverableSample = { name: 'till geez kiddingly' };

  let deliverable;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/deliverables+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/deliverables').as('postEntityRequest');
    cy.intercept('DELETE', '/api/deliverables/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (deliverable) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/deliverables/${deliverable.id}`,
      }).then(() => {
        deliverable = undefined;
      });
    }
  });

  it('Deliverables menu should load Deliverables page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('deliverable');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Deliverable').should('exist');
    cy.url().should('match', deliverablePageUrlPattern);
  });

  describe('Deliverable page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(deliverablePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Deliverable page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/deliverable/new$'));
        cy.getEntityCreateUpdateHeading('Deliverable');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliverablePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/deliverables',
          body: deliverableSample,
        }).then(({ body }) => {
          deliverable = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/deliverables+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/deliverables?page=0&size=20>; rel="last",<http://localhost/api/deliverables?page=0&size=20>; rel="first"',
              },
              body: [deliverable],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(deliverablePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Deliverable page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('deliverable');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliverablePageUrlPattern);
      });

      it('edit button click should load edit Deliverable page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Deliverable');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliverablePageUrlPattern);
      });

      it('edit button click should load edit Deliverable page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Deliverable');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliverablePageUrlPattern);
      });

      it('last delete button click should delete instance of Deliverable', () => {
        cy.intercept('GET', '/api/deliverables/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('deliverable').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliverablePageUrlPattern);

        deliverable = undefined;
      });
    });
  });

  describe('new Deliverable page', () => {
    beforeEach(() => {
      cy.visit(`${deliverablePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Deliverable');
    });

    it('should create an instance of Deliverable', () => {
      cy.get(`[data-cy="name"]`).type('eek per excepting');
      cy.get(`[data-cy="name"]`).should('have.value', 'eek per excepting');

      cy.get(`[data-cy="description"]`).type('what from');
      cy.get(`[data-cy="description"]`).should('have.value', 'what from');

      cy.get(`[data-cy="type"]`).select('TERRAFORM');

      cy.get(`[data-cy="format"]`).select('DIRECTORY_STRUCTURE');

      cy.get(`[data-cy="status"]`).select('IN_PROGRESS');

      cy.get(`[data-cy="packagePath"]`).type('mealy which');
      cy.get(`[data-cy="packagePath"]`).should('have.value', 'mealy which');

      cy.get(`[data-cy="packageSize"]`).type('28412');
      cy.get(`[data-cy="packageSize"]`).should('have.value', '28412');

      cy.get(`[data-cy="checksum"]`).type('hmph on on');
      cy.get(`[data-cy="checksum"]`).should('have.value', 'hmph on on');

      cy.get(`[data-cy="createdDate"]`).type('2025-09-14T05:59');
      cy.get(`[data-cy="createdDate"]`).blur();
      cy.get(`[data-cy="createdDate"]`).should('have.value', '2025-09-14T05:59');

      cy.get(`[data-cy="lastModifiedDate"]`).type('2025-09-13T14:42');
      cy.get(`[data-cy="lastModifiedDate"]`).blur();
      cy.get(`[data-cy="lastModifiedDate"]`).should('have.value', '2025-09-13T14:42');

      cy.get(`[data-cy="addendum"]`).type('lest in');
      cy.get(`[data-cy="addendum"]`).should('have.value', 'lest in');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        deliverable = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', deliverablePageUrlPattern);
    });
  });
});
