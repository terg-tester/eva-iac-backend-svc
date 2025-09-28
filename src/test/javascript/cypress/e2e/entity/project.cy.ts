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

describe('Project e2e test', () => {
  const projectPageUrl = '/project';
  const projectPageUrlPattern = new RegExp('/project(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const projectSample = { name: 'concrete blah functional' };

  let project;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/projects+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/projects').as('postEntityRequest');
    cy.intercept('DELETE', '/api/projects/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (project) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/projects/${project.id}`,
      }).then(() => {
        project = undefined;
      });
    }
  });

  it('Projects menu should load Projects page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Project').should('exist');
    cy.url().should('match', projectPageUrlPattern);
  });

  describe('Project page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(projectPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Project page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/project/new$'));
        cy.getEntityCreateUpdateHeading('Project');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', projectPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/projects',
          body: projectSample,
        }).then(({ body }) => {
          project = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/projects+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/projects?page=0&size=20>; rel="last",<http://localhost/api/projects?page=0&size=20>; rel="first"',
              },
              body: [project],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(projectPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Project page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('project');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', projectPageUrlPattern);
      });

      it('edit button click should load edit Project page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Project');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', projectPageUrlPattern);
      });

      it('edit button click should load edit Project page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Project');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', projectPageUrlPattern);
      });

      it('last delete button click should delete instance of Project', () => {
        cy.intercept('GET', '/api/projects/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('project').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', projectPageUrlPattern);

        project = undefined;
      });
    });
  });

  describe('new Project page', () => {
    beforeEach(() => {
      cy.visit(`${projectPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Project');
    });

    it('should create an instance of Project', () => {
      cy.get(`[data-cy="name"]`).type('brr emergent serpentine');
      cy.get(`[data-cy="name"]`).should('have.value', 'brr emergent serpentine');

      cy.get(`[data-cy="description"]`).type('although hollow');
      cy.get(`[data-cy="description"]`).should('have.value', 'although hollow');

      cy.get(`[data-cy="status"]`).select('IN_PROGRESS');

      cy.get(`[data-cy="priority"]`).select('HIGH');

      cy.get(`[data-cy="startDate"]`).type('2025-09-14T08:28');
      cy.get(`[data-cy="startDate"]`).blur();
      cy.get(`[data-cy="startDate"]`).should('have.value', '2025-09-14T08:28');

      cy.get(`[data-cy="endDate"]`).type('2025-09-14T13:11');
      cy.get(`[data-cy="endDate"]`).blur();
      cy.get(`[data-cy="endDate"]`).should('have.value', '2025-09-14T13:11');

      cy.get(`[data-cy="createdDate"]`).type('2025-09-13T22:48');
      cy.get(`[data-cy="createdDate"]`).blur();
      cy.get(`[data-cy="createdDate"]`).should('have.value', '2025-09-13T22:48');

      cy.get(`[data-cy="lastModifiedDate"]`).type('2025-09-13T19:33');
      cy.get(`[data-cy="lastModifiedDate"]`).blur();
      cy.get(`[data-cy="lastModifiedDate"]`).should('have.value', '2025-09-13T19:33');

      cy.get(`[data-cy="addendum"]`).type('advancement grouchy phooey');
      cy.get(`[data-cy="addendum"]`).should('have.value', 'advancement grouchy phooey');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        project = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', projectPageUrlPattern);
    });
  });
});
