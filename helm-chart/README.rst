Helm Charts for Deploying RENKU on Kubernetes
=============================================

Helm 2.9.1 or later (including Helm 3) is necessary as we use
the :code:`before-hook-creation` hook deletion policy. See also:
`before-hook-creation delete policy <https://github.com/kubernetes/helm/commit/1d4883bf3c85ea43ed071dff4e02cc47bb66f44f>`_.


Deploying from a Helm repository
--------------------------------

Create a values file using ``renku/values.yaml`` as a template. Then run:

.. code-block:: console

    $ helm init
    $ helm repo add renku https://swissdatasciencecenter.github.io/helm-charts/
    $ helm upgrade --install renku/renku \
        --namespace renku \
        -f my-values.yaml

See the `helm chart registry <https://swissdatasciencecenter.github.io/helm-charts/>`_ for
available versions.


Testing locally
---------------
Checkout our `developer docs <https://renku.readthedocs.io/en/latest/developer/setup.html>`_
for a detailed description of how to deploy a local version using minikube.


Building images
---------------

If you want to build the Renku images required by the chart locally,
you can do so by using ``chartpress``.

.. code-block:: console

    $ pip install chartpress
    $ chartpress --tag latest

You can the use the same ``helm upgrade`` command as above to redeploy the
services using the new images. If you ommit the ``--tag latest``,
``chartpress`` will tag the images with the current commit sha and update the
relevant values in the charts.


Tests
-----

To run tests on the deployment, use

.. code-block:: console

    $ helm test --cleanup renku


Upgrading
---------
Most information related to upgrading from one chart version to another is covered
in the `values changelog file <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/values.yaml.changelog.md>`_.
For upgrades that require some steps other than modifying the values files to be executed, we add some instructions here.

Upgrading to 0.11.0
*******************
We bump the PostgreSQL version from ``11`` to ``12.8`` and the GitLab major version from ``13`` to ``14``.
It is important to first perform the PostgreSQL upgrade, then upgrade to the ``0.11.0`` renku chart version
while keeping the GitLab version fixed, and finally upgrade the GitLab version.

1. Upgrade PostgreSQL
+++++++++++++++++++++++

If PostgreSQL was deployed as part of Renku, please follow [these instructions](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart/utils/postgres_migrations/version_upgrades/README.md) to upgrade to PostgreSQL ``12.8``.

Now the renku chart can be upgraded to the ``0.11.0`` version. Before doing this, make sure to pin the GitLab version by setting ``gitlab.image.tag`` in your values file.
If you had not pinned this version explicitly before, pin it to ``13.10.4-ce.0`` which is the default version set in the renku chart prior to the upgrade. Otherwise you can leave it at the previously pinned version.
Then deploy the new chart version through ``helm upgrade ... --version 0.11.0 ...``.

2. Upgrade GitLab
+++++++++++++++++

Please read the `GitLab documentation on this topic <https://docs.gitlab.com/ce/update>`_ before proceeding.

The following instructions assume your GitLab instance is at version ``13.10.Z``.

For each of the upgrade steps below we recommend setting the corresponding tag in the values file at ``gitlab.image.tag``, redeploy through helm, wait for the GitLab pod to be up, and make a quick test/monitor.

1. Upgrade using image 13.10.4-ce.0 (default in the renku ``0.8.0``, ``0.9.0`` and ``0.10.0`` helm chart)
2. Upgrade using image 13.12.15-ce.0
3. A few things are deprecated/unsupported in GitLab ``14``, so before upgrading to this major version you might need to:

 - upgrade PostgreSQL version to ``12.8`` (if not yet done, please follow the above ``Upgrade PostgreSQL`` instructions).
 - ``Unicorn`` is deprecated and replaced by ``Puma``, you should then `convert old Unicorn settings to Puma <https://docs.gitlab.com/ee/administration/operations/puma.html#convert-unicorn-settings-to-puma>`__.
 - migrate to hashed storage (`documentation reference <https://docs.gitlab.com/ee/administration/raketasks/storage.html#migrate-to-hashed-storage>`__), from a shell within the GitLab pod execute: ``gitlab-rake gitlab:storage:migrate_to_hashed``

4. Upgrade using image 14.0.12-ce.0 (or greater). This major version change will trigger `batched background migrations <https://docs.gitlab.com/ee/update/#batched-background-migrations>`__, these can take hours or even days and should be over before moving on to the next upgrade. To check the progress login as admin and got to Admin Area -> Monitoring -> Background Migrations.
5. Upgrade using image 14.1.Z-ce.0
6. Upgrade using image 14.2.Z-ce.0
7. Upgrade using image 14.3.Z-ce.0
8. Upgrade using image 14.4.4-ce.0 (default in the Renku ``0.11.0`` helm chart). Note that this version does not have to be selected explicitly in your own values file anymore as it is the default of the ``0.11.0`` renku chart.

Upgrading to 0.8.4
******************
We have added add a new section called `serverDefaults` to the `values.yaml` for the notebook service.
The information in this new `serverDefaults` section is used for any server options that are not specified
explicitly when launching a session. This allows a renku admin to leave out a specific option from the
`serverOptions` section and apply the value specified in the `serverDefaults` section for all sessions.
Please note that the default values specified in the  `serverDefaults` should be available as one of the options
in `serverOptions` - if the specific option appears in both sections. The defaults in the `serverOptions`
section now only refer to the default selection that is shown to the user in the UI.

This ability to use persistent volumes for user sesssions is also introduced with this release. This is optional and can be enabled in the values
file for the helm chart. In addition to enabling this feature users have the ability to select the storage class used by the persistent
volumes. We strongly recommend that a storage class with a `Delete` reclaim policy is used, otherwise persistent volumes from all user
sessions will keep accumulating.

Lastly, unlike previous versions, with 0.8.4 the amount of disk storage will be **strongly enforced**,
regardless of whether persistent volumes are used or not. With persistent volumes users will simply run out of space. However,
when persistent volumes are not used, going over the amount of storage that a user has requested when starting their session
will result in eviction of the k8s pod that runs the session and termination of the session. Therefore, admins are advised
to review and set proper options for disk sizes in the `notebooks.serverOptions` portion of the values file.

Upgrading to 0.8.0
******************
We bump the PostgreSQL version from 9.6 to 11 and the GitLab major version from 11 to 13.
It is important to first perform the PostgreSQL upgrade, then upgrade to the ``0.8.0`` chart version
while keeping the GitLab version fixed, and finally upgrade the GitLab version.

1. Upgrading postgresql
+++++++++++++++++++++++
If PostgreSQL was deployed as part of Renku, please follow `these instructions <https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart/utils/postgres_migrations/version_upgrades/README.md>`__
for the PostgreSQL upgrade.

2. Bump the chart version
+++++++++++++++++++++++++
Now it's time to upgrade to the ``0.8.0`` version of the Renku chart. Before doing this, make sure
to pin the GitLab version by setting ``gitlab.image.tag`` in your values file. If you had not pinned
this version explicitly before, pin it to ``11.9.11-ce.0`` which is the default version set in the Renku
chart prior to the upgrade. Otherwise you can leave it at the previously pinned version. Then deploy the
new chart version through ``helm upgrade ... --version 0.8.0 ...``.

3. Upgrade GitLab
+++++++++++++++++
Please read the `GitLab documentation on this topic <https://docs.gitlab.com/ce/update>`_ before proceeding.
Following the `recommended upgrade paths <https://docs.gitlab.com/ce/update/#upgrade-paths>`_ and assuming
your GitLab instance is at version ``11.9.11``, this means that your upgrade path will be
``11.11.8 -> 12.0.12 -> 12.1.17 -> 12.10.14 -> 13.0.14 -> 13.1.11 -> 13.10.4``. The corresponding
image tags are:

- 11.11.8-ce.0
- 12.0.12-ce.0
- 12.1.17-ce.0
- 12.10.14-ce.0
- 13.0.14-ce.0
- 13.1.11-ce.0
- 13.10.4-ce.0 (default in the Renku ``0.8.0`` helm chart)

For each step, set the corresponding tag in your values file at ``gitlab.image.tag``, redeploy through
helm and wait for the gitlab pod to be recreated and all migrations to finish. Repeat this procedure until
you've reached the target version of this upgrade ``13.10.4-ce.0``. Note that this version does not have
to be selected explicitly in your own values file as it is the default of the ``0.8.0`` renku chart.

Upgrading to 0.7.8
******************
This upgrade comes with an upgrade of the keycloak chart from ``4.10.2`` to ``9.8.1``! For
details on this upgrade check the dedicated section in the
`the keycloak chart docs <https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#upgrading>`_
and the `keycloak docs <https://www.keycloak.org/docs/latest/upgrading/>`_.

- Before starting, make sure to check out `the values changelog for this upgrade <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/values.yaml.changelog.md#upgrading-to-renku-080-includes-breaking-changes>`_
  and update your values file accordingly.

- The upgrade of keycloak will perform an **irreversible database migration**. It is therefore recommended
  to **back up your postgres volume** before performing this upgrade.

- **Warning: Persist keycloak-related secrets!**

  If ``global.keycloak.postgresPassword.value`` and ``global.keycloak.password.value``
  have not been explicitly defined in the values file (and thus have been autocreated by helm),
  add them to the values file now.

  * Get the ``keycloak-postgres-password`` from the ``renku-keycloak-postgres`` secret and add it as ``global.keycloak.postgresPassword.value``.
  * Get the ``keycloak-password`` from the ``keycloak-password-secret`` and add it as ``global.keycloak.password.value``.

  This should result in something like
.. code-block:: bash

    global:
      keycloack:
        postgresPassword:
          value: <actual-keycloak-postgres-password>
        password:
          value: <actual-keycloak-admin-password>


- Delete the two secrets which need to be recreated as well as the keycloak StatefulSet:

.. code-block:: bash

    kubectl delete secrets -n <namespace> keycloak-password-secret renku-keycloak-postgres
    KEYCLOAK_NAME=`kubectl get statefulsets.apps -n <namespace> -l app=keycloak --no-headers=true -o custom-columns=":metadata.name"`
    kubectl delete statefulsets.apps -n <namespace> $KEYCLOAK_NAME

- Perform the appropriate ``helm upgrade`` command to use the new chart version and your modified values file.

- If you should find yourself in the place where you have to rollback these changes, a simple ``helm rollback``
  will unfortunately not work. Instead, recover the postgres volume from your backup, remove both secrets mentioned
  above and the keycloak StatefulSet, make sure ``global.keycloak.postgresPassword.value`` and ``global.keycloak.password.value``
  set also in your original values file. Then perform an *upgrade* to the previously deployed Renku chart version.
