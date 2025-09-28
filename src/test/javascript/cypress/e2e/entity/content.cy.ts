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

describe('Content e2e test', () => {
  const contentPageUrl = '/content';
  const contentPageUrlPattern = new RegExp('/content(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const contentSample = {
    fileName: 'homely upright peter',
    dateCreated: '2025-09-16T20:48:54.989Z',
    content: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=',
    contentContentType: 'unknown',
  };

  let content;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/contents+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/contents').as('postEntityRequest');
    cy.intercept('DELETE', '/api/contents/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (content) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/contents/${content.id}`,
      }).then(() => {
        content = undefined;
      });
    }
  });

  it('Contents menu should load Contents page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('content');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Content').should('exist');
    cy.url().should('match', contentPageUrlPattern);
  });

  describe('Content page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(contentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Content page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/content/new$'));
        cy.getEntityCreateUpdateHeading('Content');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/contents',
          body: contentSample,
        }).then(({ body }) => {
          content = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/contents+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/contents?page=0&size=20>; rel="last",<http://localhost/api/contents?page=0&size=20>; rel="first"',
              },
              body: [content],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(contentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Content page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('content');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contentPageUrlPattern);
      });

      it('edit button click should load edit Content page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Content');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contentPageUrlPattern);
      });

      it('edit button click should load edit Content page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Content');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contentPageUrlPattern);
      });

      it('last delete button click should delete instance of Content', () => {
        cy.intercept('GET', '/api/contents/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('content').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contentPageUrlPattern);

        content = undefined;
      });
    });
  });

  describe('new Content page', () => {
    beforeEach(() => {
      cy.visit(`${contentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Content');
    });

    it('should create an instance of Content', () => {
      cy.get(`[data-cy="fileName"]`).type('blue sham ew');
      cy.get(`[data-cy="fileName"]`).should('have.value', 'blue sham ew');

      cy.get(`[data-cy="dateCreated"]`).type('2025-09-17T02:12');
      cy.get(`[data-cy="dateCreated"]`).blur();
      cy.get(`[data-cy="dateCreated"]`).should('have.value', '2025-09-17T02:12');

      cy.setFieldImageAsBytesOfEntity('content', 'integration-test.png', 'image/png');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        content = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', contentPageUrlPattern);
    });
  });
});
